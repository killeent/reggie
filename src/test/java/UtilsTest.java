import com.killeent.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Original Author: Trevor Killeen (2014)
 *
 * Unit tests for {@link com.killeent.Utils}.
 */
public class UtilsTest {

    // Tests for isOutboundLink

    /**
     * Tests that non-outbound links return false in a call to
     * {@link com.killeent.Utils#isOutboundLink(java.net.URL, java.net.URL)}
     */
    @Test
    public void testIsOutboundLinkFalse() {
        try {
            URL url1 = new URL("http://google.com/images");
            URL url2 = new URL("http://google.com/news");
            Assert.assertFalse(Utils.isOutboundLink(url1, url2));
        } catch (MalformedURLException e) {
            Assert.fail("Invalid format for testIsOutboundLinkFalse");
        }
    }

    /**
     * Tests that outbound links return true in a call to
     * {@link com.killeent.Utils#isOutboundLink(java.net.URL, java.net.URL)}
     */
    @Test
    public void testIsOutboundLinkTrue() {
        try {
            URL url1 = new URL("http://apple.com/iphone");
            URL url2 = new URL("http://google.com/nexus");
            Assert.assertTrue(Utils.isOutboundLink(url1, url2));
        } catch (MalformedURLException e) {
            Assert.fail("Invalid format for testIsOutboundLinkTrue");
        }
    }

    // Tests for generateImagePath

    /**
     * Tests generating an image path for a file that does not already exist
     * in the temp directory.
     */
    @Test
    public void testGenerateImagePathNewName() {
        String image = "test.com/test_image_abc.jpg";
        String temp = System.getProperty("java.io.tmpdir");
        String expected = new File(temp, "test_image_abc.jpg").getAbsolutePath();
        Assert.assertEquals(expected, Utils.generateImagePath(image, temp));
    }

    /**
     * Tests generating an image path for a file that *does* already exist in the
     * temp directory.
     */
    @Test
    public void testGenerateImagePathDupName() {
        String temp = System.getProperty("java.io.tmpdir");
        File existing;
        try {
            existing = File.createTempFile("test_image_abc", ".jpg", new File(temp));
            String expected = new File(temp, existing.getName() + "(1)").getAbsolutePath();
            Assert.assertEquals(
                expected, Utils.generateImagePath("test/" + existing.getName(), temp));
            existing.delete();
        } catch (IOException e) {
            Assert.fail("Failed to create temp file in testGenerateImagePathDupName");
        }
    }

}
