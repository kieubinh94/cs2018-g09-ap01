package cs2018.ap.streaming.utils;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@SuppressWarnings({"PMD.LongVariable"})
public final class HtmlUtils {
  private static final List<String> SHOULD_REMOVED_CHARACTERS =
      ImmutableList.of("<br />", "<br>", "&nbsp;", "&#039;");

  private HtmlUtils() {}

  public static String getText(final String html) {
    String htmlSource = html;
    for (final String removedChars : SHOULD_REMOVED_CHARACTERS) {
      htmlSource = StringUtils.replaceAll(htmlSource, removedChars, StringUtils.EMPTY);
    }

    final Document doc = Jsoup.parse(htmlSource);
    return doc.body().text();
  }
}
