package cs2018.ap.streaming.message;
import cs2018.ap.streaming.utils.DateUtils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public enum TwitterDateDeserializer {
  INSTANCE;

  public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

  public Date parse(final String dateStr) {
    try {
      return DateUtils.parseWithTz(dateStr, formatter);
    } catch (DateTimeParseException exception) {
      // dateStr is missing timezone information
      // Example: 2017-08-31T16:59:32Z
      return DateUtils.parseAssumeUtc(dateStr, formatter);
    }
  }
}
