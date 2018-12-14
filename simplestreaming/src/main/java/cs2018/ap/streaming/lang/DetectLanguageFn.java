package cs2018.ap.streaming.lang;

import cs2018.ap.streaming.message.EnrichedMessage;
import java.util.NoSuchElementException;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class DetectLanguageFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 1641620479387922626L;
  private static final Logger LOG = LoggerFactory.getLogger(DetectLanguageFn.class);

  @ProcessElement
  public void processElement(final ProcessContext context)
      throws JedisConnectionException, NoSuchElementException {
    final EnrichedMessage relMsg = context.element();

    LOG.debug("Start DetectLanguageFn with message id: {}", relMsg.getId());
    if (!relMsg.getLang().equalsIgnoreCase("en")) {
      return;
    }

    context.output(relMsg);
  }
}
