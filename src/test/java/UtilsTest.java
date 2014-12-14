import com.killeent.Utils;
import org.junit.Assert;
import org.junit.Test;

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

}
