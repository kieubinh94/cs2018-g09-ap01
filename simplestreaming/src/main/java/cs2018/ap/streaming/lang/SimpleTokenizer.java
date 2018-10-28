package cs2018.ap.streaming.lang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import cs2018.ap.streaming.utils.StringConstants;
import java.util.HashMap;
import java.util.Map;

public class SimpleTokenizer extends Tokenizer {

  private static final Map<String, String> REPLACE_CHARS = new HashMap<>();

  static {
    REPLACE_CHARS.put("’", "'");
    REPLACE_CHARS.put("`", "'");
    REPLACE_CHARS.put("…", "...");
    REPLACE_CHARS.put("(", StringConstants.SPACE);
    REPLACE_CHARS.put(")", StringConstants.SPACE);
    REPLACE_CHARS.put("[", StringConstants.SPACE);
    REPLACE_CHARS.put("]", StringConstants.SPACE);
    REPLACE_CHARS.put("{", StringConstants.SPACE);
    REPLACE_CHARS.put("}", StringConstants.SPACE);
    REPLACE_CHARS.put("<", StringConstants.SPACE);
    REPLACE_CHARS.put(">", StringConstants.SPACE);
  }

  public SimpleTokenizer() {
    super(
        ImmutableList.of(
            new LowerCaseFn(),
            new UnescapeFn(),
            new ReplaceCharFn(REPLACE_CHARS),
            new ReplaceByPatternFn(ImmutableMap.of(",([a-z])", ", $1", "([a-z]),(\\S)", "$1, $2")),
            new RemoveRetweetFn()),
        new SimpleTextToTokensFn(),
        new SimpleCleanTokensFn());
  }
}
