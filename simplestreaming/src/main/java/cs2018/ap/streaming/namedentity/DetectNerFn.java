package cs2018.ap.streaming.namedentity;

import avro.shaded.com.google.common.base.Preconditions;
import cs2018.ap.streaming.io.RedisConnector;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.lang.TextUtils;
import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.utils.StringConstants;
import java.util.*;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

public class DetectNerFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(DetectNerFn.class);

  public static final String REDIS_NSPACE_KEYWORD = "keywords";

  private static final int MAX_NGRAM_TOKENS = 4;

  private final SerializableRedisOptions redisOptions;
  private transient JedisCommands redis;

  public DetectNerFn(final SerializableRedisOptions redisOptions) {
    this.redisOptions = redisOptions;
  }

  @Setup
  public void setUp() {
    this.redis = RedisConnector.getInstance(redisOptions);
  }

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage originalMsg = context.element();
    LOG.debug("Start DenormalizeNamedEntityFn with message ID: {}", originalMsg.getId());

    Preconditions.checkNotNull(
        originalMsg.getPublisher(),
        "Missed publishedBy in enriched message. We need publishedBy to find score_topic for ne_mentions");

    final EnrichedMessage relMsg = new EnrichedMessage(originalMsg);

    List<Integer> topicIds = new ArrayList<>();
    List<String> ngrams = generateNgram(relMsg.getContent());
    for (String ngram : ngrams) {
      // try to find in Redis by ngram
      String topic = redis.hget(REDIS_NSPACE_KEYWORD, ngram);

      if (Objects.nonNull(topic)) {
        // found topic
        topicIds.add(Integer.valueOf(topic));
      }
    }
    LOG.debug(
        "Found {} named entities in Redis with text {}", topicIds.size(), relMsg.getContent());
    relMsg.setTopicIds(topicIds);

    context.output(relMsg);
  }

  @Teardown
  public void close() {
    LOG.info("Release redis connection to pool");
    RedisConnector.close(redis);
  }

  private List<String> generateNgram(String content) {
    List<String> tokens = TextUtils.tokenize(content);

    List<String> ngrams = new ArrayList<>();
    // generate ngram has multi tokens first
    for (int numberOfTokens = MAX_NGRAM_TOKENS; numberOfTokens >= 2; numberOfTokens--) {
      for (int i = 0; i < tokens.size() - numberOfTokens; i++) {
        ngrams.add(String.join(StringConstants.SPACE, tokens.subList(i, i + numberOfTokens)));
      }
    }

    // add ngram has one tokens
    ngrams.addAll(tokens);

    return ngrams;
  }
}
