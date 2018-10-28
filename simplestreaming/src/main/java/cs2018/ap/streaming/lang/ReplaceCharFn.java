package cs2018.ap.streaming.lang;

import java.util.Map;

@SuppressWarnings("PMD.AvoidReassigningParameters")
public class ReplaceCharFn implements PreprocessTextFn {

  private final transient Map<String, String> charReplacedMap;

  ReplaceCharFn(final Map<String, String> charReplacedMap) {
    this.charReplacedMap = charReplacedMap;
  }

  @Override
  public String handle(String text) {
    for (final Map.Entry<String, String> item : charReplacedMap.entrySet()) {
      text = text.replace(item.getKey(), item.getValue());
    }
    return text;
  }
}
