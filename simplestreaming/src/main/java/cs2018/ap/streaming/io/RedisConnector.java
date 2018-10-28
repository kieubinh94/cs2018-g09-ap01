package cs2018.ap.streaming.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "PMD.LongVariable", "PMD.UnusedPrivateMethod"})
public final class RedisConnector {

  private static final Logger LOG = LoggerFactory.getLogger(RedisConnector.class);
  private JedisPool jedisStandalone;

  private static Map<SerializableRedisOptions, RedisConnector> poolSynchronizedMap =
      Collections.synchronizedMap(new HashMap<>());

  private RedisConnector(final SerializableRedisOptions redisOptions) {
    LOG.info("Finding jedisCommands by {}", redisOptions);

    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(redisOptions.getRedisMaxConnect());

    jedisStandalone =
        new JedisPool(poolConfig, redisOptions.getRedisHost(), redisOptions.getRedisPort());
  }

  public static Jedis getInstance(final SerializableRedisOptions redisOptions) {
    if (!poolSynchronizedMap.containsKey(redisOptions)) {
      poolSynchronizedMap.put(redisOptions, new RedisConnector(redisOptions));
    }

    return poolSynchronizedMap.get(redisOptions).getJedisStandalone().getResource();
  }

  private JedisPool getJedisStandalone() {
    return jedisStandalone;
  }

  public static void close(final JedisCommands jedis) {
    ((Jedis) jedis).close();
  }
}
