package cs2018.ap.streaming.utils;

import java.text.BreakIterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"PMD.LongVariable", "PMD.BeanMembersShouldSerialize"})
public final class StringUtils {

  private StringUtils() {}

  /**
   * We don't want to have no-meaning word at the end of sentence. So we need to cut string at space
   * character in sentence
   *
   * <p>Example:
   *
   * <p>input string: String test = "this is a test"
   * <li>java substring: test.substring(6) = "this i"
   * <li>cutSentenceAtSpaceChar(test, 6): = "this is" -> length is 7
   */
  public static String cutSentenceAtSpaceChar(final String text, final int size) {

    if (Objects.isNull(text) || text.isEmpty()) {
      return text;
    }

    // Nothing to do if new sentence length is greater than input sentence length.
    if (size > text.length()) {
      return text;
    }

    final BreakIterator boundary = BreakIterator.getWordInstance();
    boundary.setText(text);
    final StringBuilder content = new StringBuilder();
    int start = boundary.first();
    for (int end = boundary.next();
        end != BreakIterator.DONE && content.length() < size;
        start = end, end = boundary.next()) {
      content.append(text.substring(start, end));
    }

    return content.toString().trim();
  }

  public static String concat(final String... values) {
    return Stream.of(values)
        .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
        .collect(Collectors.joining(StringConstants.SPACE));
  }

  private static String extendSpacesEscape(final String text) {
    return String.format("\\Q %s \\E", text);
  }

  public static String getMaxTokens(final List<String> tokens, final int maxLength) {
    final StringBuilder builder = new StringBuilder();
    for (int index = 0; index < tokens.size(); index++) {
      final String token = tokens.get(index);
      builder.append(token);

      if (index < tokens.size() - 1
          && builder.length() + tokens.get(index + 1).length() + 1 > maxLength) {
        return builder.toString();
      }

      builder.append(StringConstants.SPACE);
    }
    return builder.toString().trim();
  }
}
