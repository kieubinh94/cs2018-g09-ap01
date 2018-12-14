package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.io.RedisConnector;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.message.Publisher;
import cs2018.ap.streaming.publisher.MatchPublisherFn;
import cs2018.ap.streaming.utils.JacksonConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.JedisCommands;

public class RedisImporter {
  public static void main(final String[] args) throws IOException {
    List<String> lines =
        Files.readAllLines(Paths.get("simplestreaming/src/test/resources/expected-msgs.json"));

    createPublishers(lines);
  }

  private static void createPublishers(List<String> lines) throws IOException {

    final JedisCommands redis =
        RedisConnector.getInstance(new SerializableRedisOptions(100, "10.20.40.41", 6379));

    Set<String> publishers = new HashSet<>();
    for (String line : lines) {
      final Map<String, Object> data = JacksonConverter.INSTANCE.parseJsonToObject(line, Map.class);
      Map<String, Object> publisher = (Map<String, Object>) data.get("published_by");
      if (!publishers.contains(publisher.get("sns_id").toString())) {
        if (publisher.get("country_code") != null) {
          String id = publisher.get("sns_id").toString();
          Publisher pub = new Publisher();
          pub.setChannel("tw");
          pub.setPartnerId(id);
          pub.setAvatarUrl(publisher.get("avatar_url").toString());
          pub.setCountryCode(publisher.get("country_code").toString());
          pub.setDisplayName("display_name");
          pub.setScreenName("screen_name");

          System.out.println("Setting " + id);
          redis.hset(
              MatchPublisherFn.REDIS_NSPACE_PARTNER_ID, String.format("%s:%s", "tw", id), id);
        }

        publishers.add(publisher.get("sns_id").toString());
      }
    }
    RedisConnector.close(redis);
  }
}
