package cs2018.ap.streaming.lang;

import java.util.Map;

@SuppressWarnings({"PMD.LongVariable", "PMD.AvoidReassigningParameters"})
public class ReplaceByPatternFn implements PreprocessTextFn {

  private final transient Map<String, String> patternReplacedMap;

  public ReplaceByPatternFn(final Map<String, String> patternReplacedMap) {
    this.patternReplacedMap = patternReplacedMap;
  }

  @Override
  public String handle(String text) {
    for (final Map.Entry<String, String> item : patternReplacedMap.entrySet()) {
      text = text.replaceAll(item.getKey(), item.getValue());
    }
    return text;
  }
}
