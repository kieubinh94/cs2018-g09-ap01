package cs2018.ap.streaming.lang;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("PMD.MethodNamingConventions")
public class UnescapeTests {
  @Test
  public void given_msg_when_unescape_then_returnResult() {
    // GIVEN
    final String text = "&amp;";

    // WHEN
    final String result = new UnescapeFn().handle(text);

    // THEN
    Assert.assertEquals("&", result);
  }
}
