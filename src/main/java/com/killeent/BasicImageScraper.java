package com.killeent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Simple sequential implementation of a {@link com.killeent.ImageScraper}.
 */
public class BasicImageScraper implements ImageScraper {

    private final Set<String> visitedPages;   // Pages we have scraped

    public BasicImageScraper() {
        visitedPages = new HashSet<String>();
    }

    @Override
    public void scrapePage(ImageScraperParams params) {
        visitedPages.add(params.getURL().toString());
        scrapePage(params.getURL(), 0, params);
        visitedPages.clear();
    }

    /**
     * Scrapes the content of the page specified by page.
     *
     * @param page The page to scrape. Should be a new page - i.e. one we haven't visited before.
     * @param depth The current depth of links followed.
     * @param params The scraping params.
     */
    private void scrapePage(URL page, int depth, ImageScraperParams params) {
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
                Utils.downloadImage(new URL(image), path);
            } catch (MalformedURLException e) {
                // fail silently
            } catch (IOException e) {
                System.err.printf("Failed to download image: %s\n", image);
            }
        }

        // recursively scrape other pages
        if (depth < params.maxDepth()) {
            for (String link : links) {
                // check to see if we've been here before
                if (visitedPages.contains(link)) {
                    continue;
                }
                visitedPages.add(link);

                try {
                    URL linkURL = new URL(link);
                    // check if the link is outbound; if it is, only scrape it if the params
                    // allow us to follow outbound links
                    boolean outbound = Utils.isOutboundLink(params.getURL(), linkURL);
                    if (outbound && !params.followOutboundLinks()) {
                        continue;
                    }

                    // good to go!
                    scrapePage(linkURL, depth + 1, params);
                } catch (MalformedURLException e) {
                    // fail silently
                }
            }
        }
    }


}
