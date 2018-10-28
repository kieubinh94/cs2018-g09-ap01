package cs2018.ap.streaming.lang;

import java.util.Locale;

public class LowerCaseFn implements PreprocessTextFn {

  @Override
  public String handle(final String text) {
    return text.toLowerCase(Locale.ENGLISH);
  }
}
