package cs2018.ap.streaming.lang;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import cs2018.ap.streaming.utils.StringConstants;
import java.util.regex.Pattern;

@SuppressWarnings("PMD.LongVariable")
class SimpleCleanTokensFn extends CleanTokensFn {

  private static final Pattern PREFIX_REMOVE_CHARS = Pattern.compile("^[^\\w+@$&€£¥äöüÄÖÜßé]+");
  private static final Pattern SUFFIX_REMOVE_CHARS = Pattern.compile("[^\\w+$@&€£¥äöüÄÖÜßé]+$");

  private static final ImmutableMap<Pattern, String> PATTERN_MAP =
      ImmutableMap.of(
          PREFIX_REMOVE_CHARS, StringConstants.EMPTY,
          SUFFIX_REMOVE_CHARS, StringConstants.EMPTY);

  SimpleCleanTokensFn() {
    super(ImmutableSet.of());
  }

  @Override
  ImmutableMap<Pattern, String> getCheckedPattern() {
    return PATTERN_MAP;
  }
}
