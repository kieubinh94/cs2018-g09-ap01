package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.message.NamedEntity;
import cs2018.ap.streaming.message.Publisher;
import cs2018.ap.streaming.namedentity.DetectNerFn;
import cs2018.ap.streaming.utils.JacksonConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SqlDataExtractor {
  public static void main(final String[] args) throws IOException {
    List<String> lines =
        Files.readAllLines(Paths.get("src/test/resources/expected-msgs.json"));

    createKeyword(lines);
    // createTopics(lines);
  }

  private static void createPublishers(List<String> lines) throws IOException {
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

          System.out.println(
              String.format(
                  "INSERT INTO publisher(publisher_id, country_code, partner_id, avatar_url) VALUES (%s, '%s', '%s', '%s');",
                  id, pub.getCountryCode(), pub.getPartnerId(), pub.getAvatarUrl()));
        }
        publishers.add(publisher.get("sns_id").toString());
      }
    }
  }

  private static void createTopics(List<String> lines) throws IOException {
    Set<String> topics = new HashSet<>();
    for (String line : lines) {
      final Map<String, Object> data = JacksonConverter.INSTANCE.parseJsonToObject(line, Map.class);
      List<Map<String, Object>> neMentions = (List<Map<String, Object>>) data.get("ne_mentions");
      for (Map<String, Object> topic : neMentions) {
        String id = topic.get("id").toString();

        if (!topics.contains(id)) {
          NamedEntity entity = new NamedEntity();
          if (topic.get("country_code") != null) {
            List<String> paths = (List<String>) topic.get("category_name_paths");
            if (Objects.nonNull(paths)
                && paths.size() >= 3
                && paths.get(2).equalsIgnoreCase("Company")) {
              entity.setCountryCode(topic.get("country_code").toString());
              entity.setType(NamedEntity.COMPANY);
              entity.setName(topic.get("name").toString());
              entity.setId(Integer.parseInt(topic.get("id").toString()));

              System.out.println(
                  String.format(
                      "INSERT INTO named_entity(named_entity_id, country_code, name, type) VALUES (%s, '%s', '%s', 1);",
                      id, entity.getCountryCode(), entity.getName()));
            }
          }
          topics.add(id);
        }
      }
    }
  }

  private static void createKeyword(List<String> lines) throws IOException {
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
              System.out.println(
                  String.format(
                      "INSERT INTO keyword(text, named_entity_id) VALUES ('%s', %s);", name.toLowerCase(Locale.ENGLISH), id));

              String stripName =
                  name.replace(" Inc", "")
                      .replace(" Corp", "")
                      .replace(" LLC", "")
                      .replace(" Ltd", "")
                      .trim();
              if (!stripName.equalsIgnoreCase(name)) {
                System.out.println(
                    String.format(
                        "INSERT INTO keyword(text, named_entity_id) VALUES ('%s', %s);", stripName.toLowerCase(Locale.ENGLISH), id));
              }
            }
          }
          topics.add(id);
        }
      }
    }
  }
}
