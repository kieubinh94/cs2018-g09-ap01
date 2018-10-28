package cs2018.ap.streaming.message;

import cs2018.ap.streaming.io.EsDoc;
import cs2018.ap.streaming.utils.JacksonConverter;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToEsRelDocFn extends DoFn<EnrichedMessage, EsDoc> {
  public static final Logger LOG = LoggerFactory.getLogger(ToEsRelDocFn.class);

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage msg = context.element();
    LOG.debug("Start ToEsRelDocFn with message ID: {}", msg.getId());

    final String doc = JacksonConverter.INSTANCE.writeObjectToJson(msg.toMap());
    context.output(new EsDoc(msg.getId(), doc));
  }
}
