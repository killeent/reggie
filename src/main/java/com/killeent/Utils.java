package com.killeent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Util class to encompass things that don't really fit anywhere else.
 */
public class Utils {

    /**
     * Checks whether a link is outbound.
     *
     * @param original The original URL we wanted to scrape from. We compare against this URLs host.
     * @param toCheck The URL we want to check if is outbound.
     * @return True if toCheck is an outbound link.
     */
    public static boolean isOutboundLink(URL original, URL toCheck) {
        return !original.getHost().equals(toCheck.getHost());
    }

    /**
     * Retrieves the HTML string for the given URL.
     *
     * @param url The URL to connect to.
     * @throws java.io.IOException if we cannot connect to the URL for whatever reason.
     * @return the HTML of that page, as a String.
     */
    public static String getHTML(URL url) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder result = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();
        return result.toString();
    }

}
