package cs2018.ap.streaming.publisher;

import cs2018.ap.streaming.io.RedisConnector;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.message.Publisher;
import java.util.Objects;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "PMD.LongVariable"})
public class MatchPublisherFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = -4916287707068939413L;
  private static final Logger LOG = LoggerFactory.getLogger(MatchPublisherFn.class);

  public static final String REDIS_NSPACE_PARTNER_ID = "publisher:";

  private final SerializableRedisOptions redisOptions;
  private transient JedisCommands redis;

  public MatchPublisherFn(final SerializableRedisOptions redisOptions) {
    this.redisOptions = redisOptions;
  }

  @Setup
  public void setUp() {
    this.redis = RedisConnector.getInstance(redisOptions);
  }

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage relMsg = new EnrichedMessage(context.element());
    LOG.debug("Start MatchPublisherFn with message ID: {}", relMsg.getId());

    final Publisher publisher = relMsg.getPublisher();
    final String key = String.format("%s:%s", publisher.getChannel(), publisher.getPartnerId());
    final String publisherId = findPartnerId(key);
    publisher.setPublisher(Objects.nonNull(publisherId));

    context.output(relMsg);
  }

  private String findPartnerId(final String field) {
    return redis.hget(REDIS_NSPACE_PARTNER_ID, field);
  }

  @Teardown
  public void close() {
    LOG.info("Release redis connection to pool");
    RedisConnector.close(redis);
  }
}
