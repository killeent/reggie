package com.killeent;

import org.apache.commons.cli.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Specifies the CLI to Reggie.
 */
public class Reggie {

    public static final String DEPTH_FLAG = "depth";
    public static final String OUTBOUND_FLAG = "outbound";

    public static void main(String[] args) {
        ImageScraperParams params = null;

        // try and initialize the params
        try {
            params = parseCommandLineParameters(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            usage();
            System.exit(1);
        }

        // construct a placeholder scraper for now
        ImageScraper scraper = new BasicImageScraper();

        scraper.scrapePage(params);
    }

    /**
     * Static helper function to parse the command line arguments into an
     * {@link com.killeent.ImageScraperParams}.
     *
     * @param args The arguments passed to main.
     * @throws java.lang.IllegalArgumentException if the command line parameters are invalid
     * in some way. This exception will store a message indicating what was wrong.
     * @return ImageScraperParams that are determined by the users command line arguments
     * and can be passed to an a call to
     * {@link com.killeent.ImageScraper#scrapePage(ImageScraperParams)}.
     */
    public static ImageScraperParams parseCommandLineParameters(String[] args)
            throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid call to reggie: missing parameters");
        }

        // Define command line flags
        Options options = new Options();
        options.addOption(OptionBuilder.withDescription("crawl to a maximum depth of n")
                .hasArg()
                .withArgName("n")
                .withType(Integer.class)
                .create(DEPTH_FLAG));
        options.addOption(OUTBOUND_FLAG, false, "crawl outbound links");

        // Params
        ImageScraperParams params;

        // Tries to extract the data for web scraping from the command line arguments
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine;
        try {
            // will throw exception if cannot parse
            commandLine = parser.parse(options, args);

            // parse URI and directory
            String[] leftovers = commandLine.getArgs();
            if (leftovers.length != 2) {
                // we are missing something
                throw new IllegalArgumentException(
                        "Invalid call to reggie: missing URI and/or directory");
            }

            // will throw exception if not valid URI
            URL uri = new URL(leftovers[0]);

            // check if valid directory
            String directory = leftovers[1];
            File file = new File(directory);
            if (!file.isDirectory()) {
                throw new IllegalArgumentException(
                        String.format("Invalid call to reggie: %s is not a valid directory\n",
                                file.getAbsolutePath()));
            }

            // Okay we can construct the parameter builder
            ImageScraperParams.Builder builder = new ImageScraperParams.Builder(uri, directory);

            // parse flags
            builder.followOutboundLinks(commandLine.hasOption(OUTBOUND_FLAG));
            String maxDepth = commandLine.getOptionValue(DEPTH_FLAG);
            if (maxDepth != null) {
                builder.maxDepth(Integer.valueOf(maxDepth));
            }

            return builder.build();
        } catch (NumberFormatException n) {
            throw new IllegalArgumentException(n.getMessage(), n);
        } catch (ParseException p) {
            throw new IllegalArgumentException(p.getMessage(), p);
        } catch (MalformedURLException m) {
            throw new IllegalArgumentException(m.getMessage(), m);
        }
    }

    /**
     * Prints the CLI usage specifications.
     */
    private static void usage() {
        System.out.println("Usage: java Reggie [--depth=n | -outbound ] uri output_directory");
    }
}
