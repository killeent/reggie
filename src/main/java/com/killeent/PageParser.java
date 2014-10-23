package com.killeent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Class to encapsulate all logic for parsing a pages HTML and extracting image links
 * and links to other web pages.
 */
public class PageParser {

    /**
     * Raw method to extract links to pages and links to images from the given HTML. This
     * function places these links in the passed collections. It performs no scrubbing
     * of the links except to guarantee that they are absolute path'ed.
     *
     * @param html The HTML to scrape.
     * @param links The collection where we will store links to pages.
     * @param images The collection where we will store links to images.
     * @throws java.io.IOException If we cannot connect to the given URL for whatever reason.
     */
    public static void extractLinksAndImages(
            String html, Collection<String> links, Collection<String> images) throws IOException {
        Document doc = Jsoup.parse(html);

        // Get Links and Images
        Elements docLinks = doc.select("a[href]");
        Elements docImages = doc.select("img");

        // Place links in the links collection
        for (Element link : docLinks) {
            links.add(link.attr("abs:href"));
        }

        // Place images in the images collection
        for (Element img : docImages) {
            images.add(img.attr("abs:src"));
        }
    }

}
