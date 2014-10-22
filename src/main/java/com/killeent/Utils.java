package com.killeent;

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

}
