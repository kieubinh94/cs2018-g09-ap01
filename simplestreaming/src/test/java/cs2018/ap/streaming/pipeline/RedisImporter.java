package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.io.RedisConnector;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.namedentity.DetectNerFn;
import cs2018.ap.streaming.publisher.MatchPublisherFn;
import cs2018.ap.streaming.utils.JacksonConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import redis.clients.jedis.JedisCommands;

public class RedisImporter {
  public static void main(final String[] args) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get("src/test/resources/expected-msgs.json"));

    // createPublishers(lines);
    createTopics(lines);
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
          System.out.println("Setting " + id);
          redis.hset(
              MatchPublisherFn.REDIS_NSPACE_PARTNER_ID, String.format("%s:%s", "tw", id), id);
        }

        publishers.add(publisher.get("sns_id").toString());
      }
    }
    RedisConnector.close(redis);
  }

  private static void createTopics(List<String> lines) throws IOException {
    final JedisCommands redis =
        RedisConnector.getInstance(new SerializableRedisOptions(100, "10.20.40.41", 6379));

    Set<String> topics = new HashSet<>();
    for (String line : lines) {
      final Map<String, Object> data = JacksonConverter.INSTANCE.parseJsonToObject(line, Map.class);
      List<Map<String, Object>> neMentions = (List<Map<String, Object>>) data.get("ne_mentions");
      for (Map<String, Object> topic : neMentions) {
        String id = topic.get("id").toString();

        if (!topics.contains(id)) {
          if (topic.get("country_code") != null) {
            List<String> paths = (List<String>) topic.get("category_name_paths");
            if (Objects.nonNull(paths)
                && paths.size() >= 3
                && paths.get(2).equalsIgnoreCase("Company")) {
              String name = topic.get("name").toString();

              System.out.println(String.format("Setting %s - %s", id, name));
              redis.set(
                  String.format(
                      "%s:%s", DetectNerFn.REDIS_NSPACE_KEYWORD, name.toLowerCase(Locale.ENGLISH)),
                  id);

              String stripName =
                  name.replace(" Inc", "")
                      .replace(" Corp", "")
                      .replace(" LLC", "")
                      .replace(" Ltd", "")
                      .trim();
              if (!stripName.equalsIgnoreCase(name)) {
                System.out.println(String.format("Setting %s - %s", id, stripName));
                redis.set(
                    String.format(
                        "%s:%s",
                        DetectNerFn.REDIS_NSPACE_KEYWORD, stripName.toLowerCase(Locale.ENGLISH)),
                    id);
              }
            }
          }
          topics.add(id);
        }
      }
    }
    RedisConnector.close(redis);
  }
}
