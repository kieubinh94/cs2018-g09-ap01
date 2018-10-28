package cs2018.ap.streaming.lang;

import com.google.common.base.Preconditions;
import java.util.Objects;

public final class CharUtils {
  private CharUtils() {
    // final class
  }

  public static char getFirstChar(final String token) {
    Preconditions.checkArgument(Objects.nonNull(token));

    final char[] chars = token.toCharArray();
    return chars[0];
  }
}
