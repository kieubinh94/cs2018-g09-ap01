package cs2018.ap.api.utils;

import org.apache.commons.validator.routines.UrlValidator;

public class UrlUtils {
  public static boolean isValid(String url) {
    UrlValidator validator = new UrlValidator();
    return validator.isValid(url);
  }
}
