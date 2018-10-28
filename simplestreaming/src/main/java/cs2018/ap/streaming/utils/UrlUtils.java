package cs2018.ap.streaming.utils;

import com.google.common.base.Preconditions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts", "PMD.CyclomaticComplexity"})
public final class UrlUtils {

  private static final String WWW = "www";

  private static final Pattern URL_PATTERN =
      Pattern.compile("http(s)?(:|:/|://|://.+)?", Pattern.CASE_INSENSITIVE);

  private static final String[] BLOG_DOMAINS =
      new String[] {".blogspot.", ".wordpress.", ".tumblr", ".yola", ".drupal", ".typepad", ".wix"};

  private UrlUtils() {}

  public static Optional<String> stripProtocolAndQuery(final String sourceUrl) {
    if (Objects.nonNull(sourceUrl)) {
      if (isHttpProtocol(sourceUrl)) {
        final URL url = parseUrl(sourceUrl);

        final StringBuilder strippedUrl = new StringBuilder();

        strippedUrl.append(url.getHost());
        final int port = url.getPort();

        if (port != -1 && port != 80) {
          strippedUrl.append(':').append(port);
        }
        final String path = StringUtils.stripEnd(url.getPath(), StringConstants.SLASH);
        if (!path.isEmpty()) {
          strippedUrl.append(path);
        }
        String formatUrl = strippedUrl.toString();
        if (formatUrl.startsWith(WWW)) {
          formatUrl = formatUrl.substring(WWW.length() + 1);
        }
        return Optional.of(formatUrl.toLowerCase(Locale.ENGLISH));
      } else if (sourceUrl.startsWith(WWW)) {
        final int questionMarkIndex = sourceUrl.indexOf('?');
        String strippedUrl =
            sourceUrl.substring(
                WWW.length() + 1, questionMarkIndex == -1 ? sourceUrl.length() : questionMarkIndex);

        if (strippedUrl.endsWith(StringConstants.SLASH)) {
          strippedUrl = StringUtils.stripEnd(strippedUrl, StringConstants.SLASH);
        }
        return Optional.of(strippedUrl.toLowerCase(Locale.ENGLISH));
      }
    }
    return Optional.empty();
  }

  private static boolean isHttpProtocol(final String sourceUrl) {
    Preconditions.checkArgument(Objects.nonNull(sourceUrl), "sourceUrl should be not null");

    return sourceUrl.startsWith("http://") || sourceUrl.startsWith("https://");
  }

  private static URL parseUrl(final String sourceUrl) {
    try {
      return new URL(sourceUrl);
    } catch (final MalformedURLException ex) {
      // don't know how to test this case
      throw new IllegalArgumentException(String.format("Cannot parse URL '%s'", sourceUrl), ex);
    }
  }

  public static boolean isHttpUrl(final String text) {
    return URL_PATTERN.matcher(text).matches();
  }

  public static String getDomain(final String sourceUrl) {
    for (final String domain : BLOG_DOMAINS) {
      if (sourceUrl.contains(domain)) {
        return domain.split("\\.")[1];
      }
    }
    return null;
  }
}
