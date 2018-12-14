package cs2018.ap.streaming.namedentity;

import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.utils.CollectionUtils;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.slf4j.LoggerFactory;

public enum TopicFilters implements SerializableFunction<EnrichedMessage, Boolean> {
  NOT_EMPTY_NAMED_ENTITY {
    @Override
    public Boolean apply(final EnrichedMessage relMsg) {
      final boolean condition = CollectionUtils.isNotEmpty(relMsg.getTopicIds());
      if (!condition) {
        LoggerFactory.getLogger(TopicFilters.class)
            .debug("[DROP-MSG] Named Entity not found with message id: {}", relMsg.getId());
      }
      return condition;
    }
  },
}
