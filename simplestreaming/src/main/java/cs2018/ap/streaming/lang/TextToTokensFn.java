package cs2018.ap.streaming.lang;

import com.google.common.collect.ImmutableSet;
import java.util.*;

@SuppressWarnings({"PMD.AbstractNaming", "PMD.LongVariable"})
abstract class TextToTokensFn {
  private static final Set<Character> DEFAULT_IGNORE_START_CHARS =
      ImmutableSet.of('$', '@', '€', '£', '¥');
  private static final Set<String> DEFAULT_IGNORE_TOKENS = ImmutableSet.of();

  private final transient Set<String> whiteLists;

  TextToTokensFn(final Set<String> whiteLists) {
    this.whiteLists = whiteLists;
  }

  Optional<Character> getSentenceDelimiter() {
    return Optional.empty();
  }

  abstract String getTokenDelimiters();

  public List<String> handle(final String text) {
    final List<String> finalResult = new ArrayList<>();

    final List<String> tokens = TextUtils.tokenize(text);
    for (final String token : tokens) {
      // check excluded cases
      if (this.isInWhitelist(token) || this.getExcludedTokens().contains(token)) {
        finalResult.add(token);
        continue;
      }

      // check first char case
      final Character firstChar = CharUtils.getFirstChar(token);
      if (this.getExcludedByStartWithChars().contains(firstChar)) {
        if (this.getSentenceDelimiter().isPresent()) {
          finalResult.addAll(TextUtils.tokenize(token, getSentenceDelimiter().get()));
        } else {
          finalResult.add(token);
        }
        continue;
      }

      // then split tokens
      finalResult.addAll(Arrays.asList(token.split(getTokenDelimiters())));
    }
    return finalResult;
  }

  private boolean isInWhitelist(final String token) {
    return whiteLists.contains(token);
  }

  public Set<Character> getExcludedByStartWithChars() {
    return DEFAULT_IGNORE_START_CHARS;
  }

  public Set<String> getExcludedTokens() {
    return DEFAULT_IGNORE_TOKENS;
  }
}
