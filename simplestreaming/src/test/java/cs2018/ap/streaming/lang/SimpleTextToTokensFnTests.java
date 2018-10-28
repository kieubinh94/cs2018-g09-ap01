package cs2018.ap.streaming.lang;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("PMD.MethodNamingConventions")
public class SimpleTextToTokensFnTests {
  @Test
  public void given_str_when_createTokens_then_haveEnoughTokens() {
    // GIVEN
    final String msg = "d1#d2_d3=d4'd5\"d6!d7?d8-d9";

    // WHEN
    final List<String> strings = new SimpleTextToTokensFn().handle(msg);

    // THEN
    Assert.assertEquals(strings.size(), 9);
  }
}
