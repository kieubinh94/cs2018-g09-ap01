package cs2018.ap.streaming.lang;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("PMD.AbstractNaming")
abstract class CleanTokensFn {
  private final transient Set<String> whitelistWords;

  CleanTokensFn(final Set<String> whitelistWords) {
    this.whitelistWords = whitelistWords;
  }

  abstract ImmutableMap<Pattern, String> getCheckedPattern();

  List<String> handle(final List<String> tokens) {
    final List<String> mutableTokens = new ArrayList<>(tokens);

    getCheckedPattern()
        .forEach(
            (pattern, replaceBy) -> {
              for (int index = 0; index < mutableTokens.size(); index++) {
                final String token = mutableTokens.get(index);
                if (!whitelistWords.contains(token)) {
                  final String afterClean = pattern.matcher(token).replaceAll(replaceBy);
                  if (afterClean.trim().isEmpty()) {
                    mutableTokens.remove(index);
                    index--;
                  } else {
                    mutableTokens.set(index, afterClean);
                  }
                }
              }
            });

    return mutableTokens;
  }
}
