package cs2018.ap.api.utils;

import com.google.common.base.CaseFormat;

public final class StringUtils {
  public static String toLowerUnderscore(String value) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, value);
  }
}
