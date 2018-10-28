package cs2018.ap.streaming.time;

import com.google.common.collect.ImmutableSet;
import cs2018.ap.streaming.utils.DateUtils;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("PMD.MethodNamingConventions")
public class DateUtilsTests {

  @Test
  public void given_date_when_format5backWeeks_then_returnCorrectWeekStr() {
    // GIVEN
    final Date date =
        DateUtils.parseAssumeUtc(
            "2018-06-01T13:01:35", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    // WHEN
    final Set<String> months = DateUtils.format2NearestMonths(date, "yyyy_MM");

    // THEN
    Assert.assertEquals(ImmutableSet.of("2018_06", "2018_05"), months);
  }
}
