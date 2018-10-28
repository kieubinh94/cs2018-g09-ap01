package cs2018.ap.streaming.publisher;

import cs2018.ap.streaming.message.EnrichedMessage;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.slf4j.LoggerFactory;

public enum PublisherFilters implements SerializableFunction<EnrichedMessage, Boolean> {
  NOT_EMPTY_PUBLISHER {
    @Override
    public Boolean apply(final EnrichedMessage relMsg) {
      final boolean condition = relMsg.getPublisher().isPublisher();
      if (!condition) {
        LoggerFactory.getLogger(PublisherFilters.class)
            .debug("[DROP-MSG] Unknown publisher with message id: {}", relMsg.getId());
      }
      return condition;
    }
  },
}
