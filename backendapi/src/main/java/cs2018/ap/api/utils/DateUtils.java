package cs2018.ap.api.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public final class DateUtils {

  public static final String YYYY_MM_DD = "yyyy_MM_dd";
  public static final String YYYY_M = "yyyy_MM";
  public static final String YYYY_W = "yyyy_w";
  public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static final String ES_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String ES_YYYY_MM_DD = "yyyy-MM-dd";

  @SuppressWarnings("PMD.LongVariable")
  public static final String YYYY_MM_DD_PATTERN = "([0-9]{4})-([0-9]{2})-([0-9]{2})";

  private DateUtils() {}

  public static String localFormat(final Date date, final String pattern) {
    if (Objects.isNull(date)) {
      return null;
    }
    return new SimpleDateFormat(pattern, Locale.ENGLISH).format(date);
  }

  public static String format(final Date date, final String pattern) {
    if (Objects.isNull(date)) {
      return null;
    }
    final LocalDateTime ldt = date.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    final DateTimeFormatter dtf =
        DateTimeFormatter.ofPattern(
            StringUtils.defaultIfBlank(pattern, YYYY_MM_DD), Locale.ENGLISH);
    return ldt.format(dtf);
  }

  public static Date parseWithTz(final String dateStr, final DateTimeFormatter formatter) {
    final Instant instant = formatter.parse(dateStr, ZonedDateTime::from).toInstant();
    return Date.from(instant);
  }

  public static Date parseAssumeUtc(final String dateStr, final DateTimeFormatter format) {
    final LocalDateTime localDate = LocalDateTime.parse(dateStr, format);
    return Date.from(localDate.atZone(ZoneId.of("UTC")).toInstant());
  }

  public static Date parseAsUtcTimeZone(final String dateStr, final String datePattern) {
    final SimpleDateFormat sdt = new SimpleDateFormat(datePattern, Locale.US);
    sdt.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      return sdt.parse(dateStr);
    } catch (final ParseException exception) {
      // TODO : Need to check in case get exception , for Rss can replace by field created_at
      return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    }
  }

  public static Set<String> format2NearestMonths(final Date fromDate, final String pattern) {
    final LocalDateTime startLocalDateTime =
        fromDate.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    final LocalDateTime endLocalDateTime = startLocalDateTime.minusMonths(1);
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

    return ImmutableSet.of(
        formatter.format(startLocalDateTime), formatter.format(endLocalDateTime));
  }

  public static List<String> formatPastDates(
      final Date fromDate, final int daysToLookBack, final String pattern) {

    Preconditions.checkNotNull(
        fromDate, "Cannot create look back days for Sim ES Query with null created_at");
    Preconditions.checkNotNull(
        pattern, "Cannot create look back days for Sim ES Query with null pattern");
    Preconditions.checkArgument(
        daysToLookBack > 0, "Illegal argument of daysToLookBack , must be greater than 0");

    final LocalDateTime ldt = fromDate.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    final List<String> dateStrings = new ArrayList<String>();
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
    for (int i = 0; i < daysToLookBack; i++) {
      dateStrings.add(ldt.minusDays(i).format(dtf));
    }

    return dateStrings;
  }

  public static String getPastDates(
      final Date fromDate, final int daysToLookBack, final String pattern) {
    final LocalDateTime ldt = fromDate.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
    return ldt.minusDays(daysToLookBack).format(dtf);
  }

  public static String formatPastDate(
      final Date fromDate, final int daysToLookBack, final String pattern) {
    final LocalDateTime ldt = fromDate.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
    return ldt.minusDays(daysToLookBack).format(dtf);
  }

  public static String formatPastMonth(
      final Date fromDate, final int monthsToLookBack, final String pattern) {
    final LocalDateTime ldt = fromDate.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
    return ldt.minusMonths(monthsToLookBack).format(dtf);
  }

  public static boolean isValidFormat(final String date, final String pattern) {
    return date.matches(pattern);
  }

  public static int getWeekOfYearFromDate(final Date date) {
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    return calendar.get(Calendar.WEEK_OF_YEAR);
  }

  public static int getYearFromDate(final Date date) {
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    return calendar.get(Calendar.YEAR);
  }

  public static Date extractDateTimeFromTemporalAccessor(final TemporalAccessor temporalParsed) {
    try {
      return Date.from(LocalDateTime.from(temporalParsed).toInstant(ZoneOffset.UTC));
    } catch (DateTimeException ex) {
      /*
       This catch is for strings that only have the date part without time and time zone details.
       Examples: "15 april 2018" or "april 15, 2018"...
       We then can assume that time starts at 00:00 AM for these cases.
      */
      return Date.from(LocalDate.from(temporalParsed).atStartOfDay().toInstant(ZoneOffset.UTC));
    }
  }

  /**
   * This is temporaty fixes. Need to refactor to have better solution. We need to check year to
   * make sure that year is not greater than current year.
   */
  public static Date normalizeParsedDate(final Date publishedAt) {
    final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(publishedAt);
    final int year = calendar.get(Calendar.YEAR);

    if (year > currentYear) {
      return new Date();
    }

    return publishedAt;
  }

  public static int getYear(final Date date) {
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR);
  }
}
