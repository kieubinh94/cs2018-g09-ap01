package cs2018.ap.streaming.lang;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class RemoveRetweetFn implements PreprocessTextFn {

  private static final transient Pattern PATTERN =
      Pattern.compile("^rt\\s*@\\w+:", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

  @Override
  public String handle(final String text) {
    return PATTERN.matcher(text).replaceAll(StringUtils.EMPTY).trim();
  }
}
