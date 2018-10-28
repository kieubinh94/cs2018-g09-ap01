package cs2018.ap.streaming.lang;

import cs2018.ap.streaming.utils.StringConstants;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnescapeFn implements PreprocessTextFn {
  public static final Logger LOG = LoggerFactory.getLogger(UnescapeFn.class);

  @Override
  public String handle(final String text) {
    try {
      return StringEscapeUtils.unescapeHtml4(text);
    } catch (IllegalArgumentException ex) {
      LOG.debug("Invalid UTF-8 string to unescape");
      return StringConstants.EMPTY;
    }
  }
}
