package cs2018.ap.streaming.message;

import cs2018.ap.streaming.io.RedisConnector;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.exceptions.JedisConnectionException;

@SuppressWarnings({"PMD.BeanMembersShouldSerialize"})
public class DetectBlacklistFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 1641620479387922626L;
  private static final Logger LOG = LoggerFactory.getLogger(DetectBlacklistFn.class);

  private static final String REDIS_KEY = "topic:blacklist_words";

  private final SerializableRedisOptions redisOptions;
  private transient JedisCommands redis;
  private transient Set<String> blacklistWords;

  public DetectBlacklistFn(final SerializableRedisOptions redisOptions) {
    this.redisOptions = redisOptions;
  }

  @Setup
  public void setup() {
    this.redis = RedisConnector.getInstance(redisOptions);
    blacklistWords = redis.smembers(REDIS_KEY);
  }

  @ProcessElement
  public void processElement(final ProcessContext context)
      throws JedisConnectionException, NoSuchElementException {
    final EnrichedMessage relMsg = context.element();

    LOG.debug("Start DetectBlacklistFn with message id: {}", relMsg.getId());
    if (!relMsg.getPublisher().isPublisher()) {
      return;
    }

    final boolean hasBadMessage = containBlacklistKw(Arrays.asList(relMsg.getContent().split(" ")));
    relMsg.setContainBblWord(hasBadMessage);

    context.output(relMsg);
  }

  private boolean containBlacklistKw(final List<String> ngrams) {
    for (String item : ngrams) {
      if (blacklistWords.contains(item)) {
        return true;
      }
    }
    return false;
  }

  @Teardown
  public void close() {
    LOG.info("Release redis connection to pool");
    RedisConnector.close(redis);
  }
}
