package cs2018.ap.streaming.lang;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;

public class SimpleTextToTokensFn extends TextToTokensFn {

  private static final String DELIMITERS = "[#_='\"!?-]+";

  public SimpleTextToTokensFn() {
    super(ImmutableSet.of());
  }

  Optional<Character> getSentenceDelimiter() {
    return Optional.of('\'');
  }

  String getTokenDelimiters() {
    return DELIMITERS;
  }
}
