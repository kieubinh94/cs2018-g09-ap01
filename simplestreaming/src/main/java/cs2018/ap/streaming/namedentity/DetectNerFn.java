package cs2018.ap.streaming.namedentity;

import avro.shaded.com.google.common.base.Preconditions;
import cs2018.ap.streaming.message.EnrichedMessage;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectNerFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(DetectNerFn.class);

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage originalMsg = context.element();
    LOG.debug("Start DenormalizeNamedEntityFn with message ID: {}", originalMsg.getId());

    Preconditions.checkNotNull(
        originalMsg.getPublisher(),
        "Missed publishedBy in enriched message. We need publishedBy to find score_topic for ne_mentions");

    final EnrichedMessage relMsg = new EnrichedMessage(originalMsg);
    context.output(relMsg);
  }
}
