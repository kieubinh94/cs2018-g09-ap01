package cs2018.ap.streaming.namedentity;

import avro.shaded.com.google.common.base.Preconditions;
import cs2018.ap.streaming.io.RedisConnector;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.message.EnrichedMessage;
import java.util.Collections;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

public class DetectNerFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(DetectNerFn.class);

  public static final String REDIS_NSPACE_KEYWORD = "keywords";

  private final SerializableRedisOptions redisOptions;
  private transient JedisCommands redis;

  public DetectNerFn(final SerializableRedisOptions redisOptions) {
    this.redisOptions = redisOptions;
  }

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage originalMsg = context.element();
    LOG.debug("Start DenormalizeNamedEntityFn with message ID: {}", originalMsg.getId());

    Preconditions.checkNotNull(
        originalMsg.getPublisher(),
        "Missed publishedBy in enriched message. We need publishedBy to find score_topic for ne_mentions");

    final EnrichedMessage relMsg = new EnrichedMessage(originalMsg);
    relMsg.setTopicIds(Collections.singletonList(24495));

    context.output(relMsg);
  }

  @Teardown
  public void close() {
    LOG.info("Release redis connection to pool");
    RedisConnector.close(redis);
  }
}
