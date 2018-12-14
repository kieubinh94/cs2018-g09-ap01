package cs2018.ap.crawler;

import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Publisher {

  public static void main(String[] args) throws IOException {
    final PubsubClient pubsubClient = new PubsubClient(Constants.PROJECT);
    final TopicName topicName = ProjectTopicName.of(Constants.PROJECT, Constants.TOPIC);

    List<String> lines = Files.readAllLines(Paths.get("twitter-crawler/src/main/resources/input-msgs.json"));
    for (String line : lines) {
      System.out.println("Posting: " + line);
      pubsubClient.publishMessage(topicName, line);
    }
  }
}
