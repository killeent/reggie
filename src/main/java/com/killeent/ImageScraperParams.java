package com.killeent;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Parameters to pass to an Image Scraper which specify the behavior
 * of a call to {@link com.killeent.ImageScraper#scrapePage(java.net.URI, String,
 * ImageScraperParams)}
 */
public class ImageScraperParams {

    private int maxDepth;
    private boolean followOutboundLinks;

    private int maxDepth() {
        return maxDepth;
    }

    private boolean followOutboundLinks() {
        return followOutboundLinks;
    }

    /**
     * Constructs ImageScraperParams that define behavior when scraping pages
     * for images.
     *
     * @param maxDepth The maximum depth of web pages we should explore when
     *                 crawling the web. If this value is 0, we will only
     *                 scrape images from the initial page specified by the URL.
     *                 If it is 1, we will crawl all pages linked from the specified
     *                 URL and scrape images from those pages as well. If it is 2,
     *                 we will crawl all pages linked from pages linked from URL. And
     *                 so forth.
     * @param followOutboundLinks If true, we will crawl links to pages outside of the
     *                            base URL. For example, if our url is specified by
     *                            "https://abc.com/pics", and this is false, we will
     *                            only crawl pages with "abc.com" as their base path.
     *                            If true, we could jump to another site, e.g. if
     *                            /pics links to "https://xyz.com/images"
     */
    public ImageScraperParams(int maxDepth, boolean followOutboundLinks) {
        this.maxDepth = maxDepth;
        this.followOutboundLinks = followOutboundLinks;
    }

}
