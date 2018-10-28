package cs2018.ap.streaming.message;

import org.apache.beam.sdk.transforms.SerializableFunction;
import org.slf4j.LoggerFactory;

public enum MessageFilters implements SerializableFunction<EnrichedMessage, Boolean> {
  NOT_BLACKLIST {
    @Override
    public Boolean apply(final EnrichedMessage relMsg) {
      final boolean condition = relMsg.getPublisher().isPublisher();
      if (!condition) {
        LoggerFactory.getLogger(MessageFilters.class)
            .debug("[DROP-MSG] Blacklist filtering with message id:{}", relMsg.getId());
      }
      return condition;
    }
  }
}
