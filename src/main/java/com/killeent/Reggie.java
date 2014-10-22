package com.killeent;

import org.apache.commons.cli.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Specifies the CLI to Reggie.
 */
public class Reggie {

    private static final int DEFAULT_MAX_DEPTH = 3;
    private static final boolean DEFAULT_FOLLOW_OUTBOUND_LINKS = false;

    private static final String DEPTH_FLAG = "depth";
    private static final String OUTBOUND_FLAG = "outbound";

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
            System.exit(1);
        }

        // Define command line flags
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt(DEPTH_FLAG)
                                       .withDescription("crawl to a maximum depth of n")
                                       .hasArg()
                                       .withArgName("n")
                                       .create());
        options.addOption(OUTBOUND_FLAG, false, "crawl outbound links");

        // Necessary arguments
        boolean followOutboundLinks = DEFAULT_FOLLOW_OUTBOUND_LINKS;
        int maxDepth = DEFAULT_MAX_DEPTH;
        URI uri = null;
        String directory = null;

        // Tries to extract the data for web scraping from the command line arguments
        CommandLineParser parser = new BasicParser();
        CommandLine commandLine;
        try {
            // will throw exception if cannot parse
            commandLine = parser.parse(options, args);

            // parse flags
            followOutboundLinks = commandLine.hasOption(OUTBOUND_FLAG);
            maxDepth = Integer.parseInt(
                    commandLine.getOptionValue(DEPTH_FLAG, String.valueOf(DEFAULT_MAX_DEPTH)));

            // parse URI and directory
            String[] leftovers = commandLine.getArgs();
            if (leftovers.length != 2) {
                // we are missing something
                usage();
                System.exit(1);
            }

            // will throw exception if not valid URI
            uri = new URI(leftovers[0]);

            // check if valid directory
            directory = leftovers[1];
            File file = new File(directory);
            if (!file.isDirectory()) {
                System.out.println("Invalid Directory");
                System.exit(1);
            }
        } catch (ParseException p) {
            usage();
            System.exit(1);
        } catch (URISyntaxException e) {
            System.out.println("Invalid URI");
            System.exit(1);
        }

        // now construct the parameters
        ImageScraperParams params = new ImageScraperParams(maxDepth, followOutboundLinks);

        // okay we have everything, let's scrape some images! (placeholder for now)
        ImageScraper scraper = new ImageScraper() {
            @Override
            public void scrapePage(URI url, String path, ImageScraperParams params) {

            }
        };

        scraper.scrapePage(uri, directory, params);
    }

    private static void usage() {
        System.out.println("Usage: java Reggie [-depth=n | -outbound ] uri output_directory");
    }
}
