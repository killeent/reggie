package com.killeent;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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

    /**
     * Generates a unique path for an image in the given directory. Will attempt
     * to use the image name first. This function is not synchronized. The caller
     * must ensure that multiple calls to this function with the same name may generate
     * the same path if the image is not downloaded in between the calls.
     *
     * @param imageLink The URL link of the image to download.
     * @param directory The directory to download the image to.
     * @return The absolute path of where to download the image as a combination of the directory
     * and the image's name. Returns NULL if a path cannot be generated
     */
    public static String generateImagePath(String imageLink, String directory) {
        String imageName = imageLink.substring(imageLink.lastIndexOf('/') + 1);
        File f = new File(directory, imageName);

        int i = 1;
        while(f.exists() && i < 100) {
            f = new File(directory, String.format("%s(%d)", imageName, i));
            i++;
        }

        return f.exists() ? null : f.getAbsolutePath();
    }

    /**
     * Downloads the image at url to the given directory.
     *
     * @param image The URL of the image to download.
     * @param path Local name of file to download the image to.
     * @throws java.io.IOException if we cannot connect to the URL for whatever reason.
     */
    public static void downloadImage(URL image, String path) throws IOException {
        ReadableByteChannel channel = Channels.newChannel(image.openStream());
        FileOutputStream outputStream = new FileOutputStream(path);
        outputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
    }

}
