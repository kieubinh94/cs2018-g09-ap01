package cs2018.ap.streaming.utils;

import java.util.regex.Pattern;

public final class NumberUtils {
  private static final Pattern NUMBER_PATTERN =
      Pattern.compile("[.+\\-$¢£¥฿₣€₹\u20BD]?\\d+((\\.\\d+)|(,\\d+))*(st|nd|rd|th|m|b|bn|bln|%)?$");

  private NumberUtils() {}

  public static boolean isNumberOrMoney(final String text) {
    return NUMBER_PATTERN.matcher(text).matches();
  }
}
