package com.killeent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Parallel (ThreadPool) implementation of a {@link com.killeent.ImageScraper}.
 */
public class ParallelImageScraper implements ImageScraper {

    private final Set<String> visitedPages;         // pages we have scraped
    private final Object pageLock;                  // guards the visited pages set
    private final Set<String> visitedImages;        // images we have scraped
    private final Object imageLock;                 // guards the visited images set
    private final ExecutorService executor;         // executor for parallel scraping
    private final RecursiveTaskManager taskManager; // keeps track of currently executing tasks

    private static final long SCRAPER_TIMEOUT = 60 * 1000;  // 60s

    public ParallelImageScraper() {
        visitedPages = new HashSet<String>();
        pageLock = new Object();
        visitedImages = new HashSet<String>();
        imageLock = new Object();
        executor = Executors.newCachedThreadPool();
        taskManager = new RecursiveTaskManager();
    }

    @Override
    public void scrapePage(ImageScraperParams params) {
        visitedPages.add(params.getURL().toString());
        PageScraper scraper = new PageScraper(params.getURL(), 0, params);
        taskManager.queueTask();
        executor.execute(scraper);
        try {
            taskManager.awaitCompletion(SCRAPER_TIMEOUT);
        } catch (InterruptedException e) {
            System.err.printf("Scraping was interrupted\n");
        } finally {
            executor.shutdown();
            visitedPages.clear();
            visitedImages.clear();
        }
    }

    /**
     * Runnable for scraping a Page.
     */
    private class PageScraper implements Runnable {

        private final URL page;
        private final int depth;
        private final ImageScraperParams params;

        /**
         * Scrapes the content of the page specified by page.
         *
         * @param page The page to scrape. Should be a new page - i.e. one we haven't visited before.
         * @param depth The current depth of links followed.
         * @param params The scraping params.
         */
        private PageScraper(URL page, int depth, ImageScraperParams params) {
            this.page = page;
            this.depth = depth;
            this.params = params;
        }

        @Override
        public void run() {
            System.out.printf("Scraping page: %s/%s\n", page.getHost(), page.getPath());

            Collection<String> links = new LinkedList<String>();
            Collection<String> images = new LinkedList<String>();
            try {
                // get the HTML for the page
                String html = Utils.getHTML(page);

                // parse it
                PageParser.extractLinksAndImages(html, links, images);

            } catch (IOException e) {
                System.err.printf(
                        "Failed to scrape page: %s; error: %s\n", page.toString(), e.getMessage());
                taskManager.taskComplete();
                return;
            }

            // download the images
            for (String image : images) {
                synchronized (imageLock) {
                    if (visitedImages.contains(image)) {
                        continue;
                    }
                    visitedImages.add(image);
                }

                // create a path for the uimage
                String path = Utils.generateImagePath(image, params.getDirectory());
                if (path == null) {
                    continue;
                }

                try {
                    System.out.printf("Downloading Image: %s\n", image);
                    ImageDownloader downloader = new ImageDownloader(new URL(image), path);
                    taskManager.queueTask();
                    executor.execute(downloader);
                } catch (IOException e) {
                    System.err.printf("Failed to download image: %s\n", image);
                }
            }

            // recursively scrape other pages
            if (depth < params.maxDepth()) {
                for (String link : links) {
                    synchronized (pageLock) {
                        // check to see if we've been here before
                        if (visitedPages.contains(link)) {
                            continue;
                        }
                        visitedPages.add(link);
                    }

                    try {
                        URL linkURL = new URL(link);
                        // check if the link is outbound; if it is, only scrape it if the params
                        // allow us to follow outbound links
                        boolean outbound = Utils.isOutboundLink(params.getURL(), linkURL);
                        if (outbound && !params.followOutboundLinks()) {
                            continue;
                        }

                        // good to go!
                        PageScraper scraper = new PageScraper(linkURL, depth + 1, params);
                        taskManager.queueTask();
                        executor.execute(scraper);
                    } catch (MalformedURLException e) {
                        // fail silently
                    }
                }
            }

            taskManager.taskComplete();
        }
    }

    /**
     * Runnable for downloading an image. Wraps a call to
     * {@link com.killeent.Utils#downloadImage(java.net.URL, String)}.
     */
    private class ImageDownloader implements Runnable {

        private final URL image;
        private final String path;

        public ImageDownloader(URL image, String path) {
            this.image = image;
            this.path = path;
        }

        @Override
        public void run() {
            try {
                Utils.downloadImage(image, path);
            } catch (IOException e) {
                System.err.printf("Failed to download image: %s\n", image);
            } finally {
                taskManager.taskComplete();
            }
        }
    }

    /**
     * Helper class to manage the recursive scraping. Used to block in
     * {@link com.killeent.ParallelImageScraper#scrapePage(ImageScraperParams)} to wait for
     * alll the recursive tasks to finish.
     */
    private class RecursiveTaskManager {
        private int value = 0;
        private Object lock = new Object();

        public void queueTask() {
            synchronized (lock) {
                value++;
            }
        }

        public void taskComplete() {
            synchronized (lock) {
                value--;
                if (value == 0) {
                    lock.notifyAll();
                }
            }
        }

        public void awaitCompletion(long timeout) throws InterruptedException {
            synchronized (lock) {
                while (value > 0) {
                    lock.wait(timeout);
                }
            }
        }

    }
}
