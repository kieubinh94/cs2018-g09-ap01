package cs2018.ap.streaming.message;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

@SuppressWarnings({"PMD.BeanMembersShouldSerialize"})
public class DetectBlacklistFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 1641620479387922626L;
  private static final Logger LOG = LoggerFactory.getLogger(DetectBlacklistFn.class);

  @ProcessElement
  public void processElement(final ProcessContext context)
      throws JedisConnectionException, NoSuchElementException {
    final EnrichedMessage relMsg = context.element();

    LOG.debug("Start DetectBlacklistFn with message id: {}", relMsg.getId());
    if (!relMsg.getPublisher().isPublisher()) {
      // we don't need to detect blacklist in this case
      // but we still need to output msg for raw token pipeline.
      context.output(relMsg);
      return;
    }

    final boolean hasBadMessage = containBlacklistKw(Arrays.asList(relMsg.getContent().split(" ")));
    relMsg.setContainBblWord(hasBadMessage);

    context.output(relMsg);
  }

  private boolean containBlacklistKw(final List<String> ngrams) {

    return false;
  }
}
