package com.killeent;

import java.io.IOException;
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

    private Set<String> visitedPages;   // Pages we have scraped

    public BasicImageScraper() {
        visitedPages = new HashSet<String>();
    }

    @Override
    public void scrapePage(ImageScraperParams params) {
        scrapePage(params.getURL(), 0, params);
    }

    /**
     * Scrapes the content of the page specified by page.
     *
     * @param page The page to scrape. Should be a new page - i.e. one we haven't visited before.
     * @param depth The current depth of links followed.
     * @param params The scraping params.
     */
    private void scrapePage(URL page, int depth, ImageScraperParams params) {
        try {
            // get the HTML for the page
            String html = Utils.getHTML(page);

            // parse it
            Collection<String> links = new LinkedList<String>();
            Collection<String> images = new LinkedList<String>();
            PageParser.extractLinksAndImages(html, links, images);

            // download the images
            for (String image : images) {
                URL imageURL = new URL(image);
                String path = Utils.generateImagePath(imageURL, params.getDirectory());
                if (path == null) {
                    continue;
                }
                Utils.downloadImage(imageURL, path);
            }

            // recursively scrape other pages
            if (depth < params.maxDepth()) {
                for (String link : links) {
                    // check to see if we've been here before
                    if (visitedPages.contains(link)) {
                        continue;
                    }
                    visitedPages.add(link);

                    URL linkURL = new URL(link);
                    // check if the link is outbound; if it is, only scrape it if the params
                    // allow us to follow outbound links
                    boolean outbound = Utils.isOutboundLink(params.getURL(), linkURL);
                    if (outbound && !params.followOutboundLinks()) {
                        continue;
                    }

                    // good to go!
                    scrapePage(linkURL, depth + 1, params);
                }
            }

        } catch (IOException e) {
            System.err.printf(
                    "Failed to scrape page: %s; error: %s\n", page.toString(), e.getMessage());
        }
    }


}
