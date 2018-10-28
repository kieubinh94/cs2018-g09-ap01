package cs2018.ap.streaming.lang;

import cs2018.ap.streaming.utils.StringConstants;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("PMD.LongVariable")
public class Tokenizer {

  private final transient List<PreprocessTextFn> preprocessers;

  private final transient TextToTokensFn textToTokensFn;
  private final transient CleanTokensFn cleanTokensFn;

  Tokenizer(
      final List<PreprocessTextFn> preprocessers,
      final TextToTokensFn textToTokensFn,
      final CleanTokensFn cleanTokensFn) {
    this.preprocessers = preprocessers;
    this.textToTokensFn = textToTokensFn;
    this.cleanTokensFn = cleanTokensFn;
  }

  public String preProcess(final String text) {
    if (StringUtils.isBlank(text)) {
      return StringConstants.EMPTY;
    }

    return String.join(StringConstants.SPACE, buildTokens(text));
  }

  public List<String> buildTokens(final String text) {

    String processingText = text;
    for (final PreprocessTextFn preprocesser : preprocessers) {
      processingText = preprocesser.handle(processingText);
    }

    final List<String> tokens = textToTokensFn.handle(processingText);
    return cleanTokensFn.handle(tokens);
  }
}
