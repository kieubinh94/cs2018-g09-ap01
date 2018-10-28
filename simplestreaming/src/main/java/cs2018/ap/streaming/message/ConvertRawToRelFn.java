package cs2018.ap.streaming.message;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertRawToRelFn extends DoFn<RawMessage, EnrichedMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(ConvertRawToRelFn.class);

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final RawMessage rawMessage = context.element().cloneMsg();
    LOG.debug("Start ConvertRawToRelFn with message ID: {}", rawMessage.getId());
    final EnrichedMessage relMsg = rawMessage.toEnrichedMsg();
    relMsg.setContent(StringEscapeUtils.unescapeHtml4(relMsg.getContent()));
    context.output(relMsg);
  }
}
