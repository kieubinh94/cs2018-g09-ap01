package cs2018.ap.streaming.lang;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("PMD.MethodNamingConventions")
public class ReplaceByPatternFnTests {
  @Test
  public void given_text_when_replace_then_haveGoodTokens() {
    // GIVEN
    final String text = "a,b";

    // WHEN
    final String actual = new ReplaceByPatternFn(ImmutableMap.of(",([a-z])", ", $1")).handle(text);

    // THEN
    Assert.assertEquals("a, b", actual);
  }
}
