package com.killeent;

import java.net.URI;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * An Image Scraper takes a URL, output folder, and a number of parameters
 * and scrapes the web page at specified URL and potentially pages it links to,
 * downloading images from those pages to the output folder.
 */
public interface ImageScraper {

    /**
     * Scrapes the web page specified by {@code url}, downloading any images
     * found into the directory specified by {@code path}. May recursively
     * crawl web pages linked from {@code url} and download images from those
     * pages as well, according to the specifications in {@code params}.
     *
     * @param url The page to scrape.
     * @param path The path representing the directory in which to save images.
     * @param params Parameters that specify behavior of the scraping.
     */
    void ScrapePage(URI url, String path, ImageScraperParams params);

}
