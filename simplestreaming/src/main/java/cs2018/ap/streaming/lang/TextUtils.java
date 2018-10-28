package cs2018.ap.streaming.lang;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import java.util.List;

public final class TextUtils {

  private TextUtils() {}

  public static List<String> tokenize(final String text, final Character delimiter) {
    return Splitter.on(CharMatcher.is(delimiter))
        .omitEmptyStrings()
        .trimResults()
        .splitToList(text);
  }

  public static List<String> tokenize(final String text) {
    return Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().trimResults().splitToList(text);
  }
}
