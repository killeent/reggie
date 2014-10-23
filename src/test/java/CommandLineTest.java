import com.killeent.Reggie;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Unit tests for command line parsing.
 */
public class CommandLineTest {

    // Tests for things going wrong

    /**
     * Tests that an exception is thrown if there are no command line arguments
     * passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCommandLineArguments() {
        Reggie.parseCommandLineParameters(new String[0]);
    }

    /**
     * Tests that an exception is thrown if there is a single command line argument
     * passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSingleCommandLineArgument() {
        Reggie.parseCommandLineParameters(new String[]{"hi"});
    }

    /**
     * Tests that an exception is thrown if the only argument is the outbound flag.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOutboundFlagOnly() {
        Reggie.parseCommandLineParameters(
                new String[]{String.format("-%s", Reggie.OUTBOUND_FLAG)});
    }

    /**
     * Tests that an exception is thrown if the only argument is the depth flag.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMaxDepthOnly() {
        Reggie.parseCommandLineParameters(
                new String[]{String.format("-%s=10", Reggie.DEPTH_FLAG)});
    }

    /**
     * Tests than an exception is thrown if the only arguments are the optional
     * flags.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOptionalParametersOnly() {
        Reggie.parseCommandLineParameters(
                new String[]{String.format("-%s", Reggie.OUTBOUND_FLAG),
                        String.format("-%s=10", Reggie.DEPTH_FLAG)});
    }

    /**
     * Tests that an exception is thrown if the URL is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidURL() {
        try {
            Reggie.parseCommandLineParameters(
                    new String[]{"adjkada", System.getProperty("java.io.tmpdir")});
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getCause().getClass(), MalformedURLException.class);
            throw e;
        }
    }

    /**
     * Tests that an exception is thrown if the directory is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDirectory() {
        Reggie.parseCommandLineParameters(
                new String[]{"http://google.com", "not_a_real_directory8923892"});
    }

    /**
     * Tests that an exception is thrown if the value passed to maxDepth cannot
     * be parsed to an integer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void tastInvalidMaxDepthValue() {
        try {
            Reggie.parseCommandLineParameters(
                    new String[]{
                            String.format("--%s=asdads", Reggie.DEPTH_FLAG),
                            "http://google.com",
                            System.getProperty("java.io.tmpdir")});
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getCause().getClass(), NumberFormatException.class);
            throw e;
        }
    }
}
