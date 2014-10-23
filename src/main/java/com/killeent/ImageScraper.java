package com.killeent;

import java.net.URI;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * An Image Scraper takes a {@link com.killeent.ImageScraperParams} and scrapes
 * the web for images according to the specifications in those parameters.
 */
public interface ImageScraper {

    /**
     * Scrapes the web page specified by according to the specifications
     * in {@code params}.
     *
     * @param params Parameters that specify behavior of the scraping.
     */
    void scrapePage(ImageScraperParams params);

}
