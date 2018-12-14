package cs2018.ap.streaming.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DataExtractor {
  public static void main(String[] args) throws IOException {
    List<String> lines =
        Files.readAllLines(Paths.get("twitter-crawler/src/main/resources/expected-msgs.json"));
    for (String line : lines) {}
  }
}
