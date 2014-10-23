package com.killeent;

import java.net.URL;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Parameters to pass to an Image Scraper which specify the behavior
 * of a call to {@link com.killeent.ImageScraper#scrapePage(ImageScraperParams)}
 */
public class ImageScraperParams {

    // Here is where we specify the default behavior for parameters in ScrapeImageParams. We need
    // to specify defaults for every single optional parameter.
    public static final int DEFAULT_MAX_DEPTH = 3;
    public static final boolean DEFAULT_FOLLOW_OUTBOUND_LINKS = false;

    // The parameters themselves
    private final URL url;
    private final String directory;
    private final int maxDepth;
    private final boolean followOutboundLinks;

    public URL getURL() {
        return url;
    }

    public String getDirectory() {
        return directory;
    }

    public int maxDepth() {
        return maxDepth;
    }

    public boolean followOutboundLinks() {
        return followOutboundLinks;
    }

    private ImageScraperParams(Builder builder) {
        this.url = builder.url;
        this.directory = builder.directory;
        this.maxDepth = builder.maxDepth;
        this.followOutboundLinks = builder.followOutboundLinks;
    }

    public static class Builder {
        // Required Parameters
        private final URL url;
        private final String directory;

        // Optional Parameters
        private int maxDepth = DEFAULT_MAX_DEPTH;
        private boolean followOutboundLinks = DEFAULT_FOLLOW_OUTBOUND_LINKS;

        /**
         * Constructs a {@link com.killeent.ImageScraperParams} builder with the required
         * parameters.
         *
         * @param url The URL of the webpage to scrape.
         * @param directory The file directory where we should place the downloaded images.
         */
        public Builder(URL url, String directory) {
            this.url = url;
            this.directory = directory;
        }

        /**
         * Sets the maximum depth of web pages we should explore when crawling the web.
         * If this value is 0, we will only scrape images from the initial page specified
         * by the URL. If it is 1, we will crawl all pages linked from the specified
         * URL and scrape images from those pages as well. If it is 2, we will crawl all
         * pages linked from pages linked from URL. And so forth.
         *
         * @param maxDepth the maximum depth.
         * @return the Builder object.
         */
        public Builder maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        /**
         *  If true, we will crawl links to pages outside of the base URL. For example, if
         *  our url is specified by "https://abc.com/pics", and this is false, we will
         *  only crawl pages with "abc.com" as their host. If true, we could jump to site
         *  with a different host, /pics links to "https://xyz.com/images"
         *
         * @param followOutboundLinks Whether to follow outbound links.
         * @return the Builder object.
         */
        public Builder followOutboundLinks(boolean followOutboundLinks) {
            this.followOutboundLinks = followOutboundLinks;
            return this;
        }

        public ImageScraperParams build() {
            return new ImageScraperParams(this);
        }
    }

}
