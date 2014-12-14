package com.killeent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Parallel (ThreadPool) implementation of a {@link com.killeent.ImageScraper}.
 */
public class ParallelImageScraper implements ImageScraper {

    private final Set<String> visitedPages;   // pages we have scraped
    private final Lock pageLock;              // guards the visited pages set
    private final ExecutorService executor;   // executor for parallel scraping

    public ParallelImageScraper() {
        visitedPages = new HashSet<String>();
        pageLock = new ReentrantLock();
        executor = Executors.newCachedThreadPool();
    }

    @Override
    public void scrapePage(ImageScraperParams params) {
        visitedPages.add(params.getURL().toString());
        PageScraper scraper = new PageScraper(params.getURL(), 0, params);
        executor.execute(scraper);
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.printf("Scraping was interrupted\n");
        } finally {
            visitedPages.clear();
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
            }

            // download the images
            for (String image : images) {
                String path = Utils.generateImagePath(image, params.getDirectory());
                if (path == null) {
                    continue;
                }
                try {
                    System.out.printf("Downloading Image: %s\n", image);
                    ImageDownloader downloader = new ImageDownloader(new URL(image), path);
                    executor.execute(downloader);
                } catch (IOException e) {
                    System.err.printf("Failed to download image: %s\n", image);
                }
            }

            // recursively scrape other pages
            if (depth < params.maxDepth()) {
                for (String link : links) {
                    pageLock.lock();
                    // check to see if we've been here before
                    if (visitedPages.contains(link)) {
                        continue;
                    }
                    visitedPages.add(link);
                    pageLock.unlock();

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
                        executor.execute(scraper);
                    } catch (MalformedURLException e) {
                        // fail silently
                    }
                }
            }
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
            }
        }
    }
}
