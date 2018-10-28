package cs2018.ap.streaming.io;

import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.utils.JacksonConverter;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToRelJsonFn extends DoFn<EnrichedMessage, String> {
  public static final Logger LOG = LoggerFactory.getLogger(ToRelJsonFn.class);

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage message = context.element();
    LOG.debug("Start ToRelJsonFn with message ID: {}", message.getId());

    context.output(JacksonConverter.INSTANCE.writeObjectToJson(message.toMap()));
  }
}
