package cs2018.ap.streaming.message;

import cs2018.ap.streaming.utils.JacksonConverter;
import java.io.IOException;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadJsonToRawMessageFn extends DoFn<String, RawMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(ReadJsonToRawMessageFn.class);

  private static final long serialVersionUID = -8915079046705420120L;

  private static final String TW_SOURCE_FIELD = "\"_source\"";
  private static final String RSS_BASE_FIELD = "\"_base\"";

  @ProcessElement
  public void processElement(final ProcessContext context) throws IOException {
    LOG.debug("Start ReadJsonToRawMessageFn");
    final String rawJsonString = context.element();
    if (isTwitterRaw(rawJsonString)) {
      context.output(
          JacksonConverter.INSTANCE.parseJsonToObject(rawJsonString, TwitterMessage.class));
    } else if (isRssRaw(rawJsonString)) {
      context.output(JacksonConverter.INSTANCE.parseJsonToObject(rawJsonString, RssMessage.class));
    }
  }

  private boolean isTwitterRaw(final String rawMessage) {
    return StringUtils.contains(rawMessage, TW_SOURCE_FIELD);
  }

  private boolean isRssRaw(final String rawMessage) {
    return StringUtils.contains(rawMessage, RSS_BASE_FIELD);
  }
}
