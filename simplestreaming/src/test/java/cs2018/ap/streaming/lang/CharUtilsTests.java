package cs2018.ap.streaming.lang;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("PMD.MethodNamingConventions")
public class CharUtilsTests {

  @Test(expected = IllegalArgumentException.class)
  public void given_nullStr_when_getFirstChar_then_throwException() {
    // GIVEN
    final String input = null;

    // WHEN
    CharUtils.getFirstChar(input);
  }

  @Test
  public void given_validStr_when_getFirstChar_then_getFirstChar() {
    // GIVEN
    final String input = "this is a string";

    // WHEN
    final char first = CharUtils.getFirstChar(input);

    // THEN
    Assert.assertEquals('t', first);
  }
}
