package cs2018.ap.streaming.lang;

import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.message.Language;
import java.io.IOException;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class TikaDetectLanguageFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 2770909625979213883L;
  private static final Logger LOG = LoggerFactory.getLogger(TikaDetectLanguageFn.class);

  private transient LanguageDetector detector;

  @Setup
  public void setup() throws IOException {
    detector = new OptimaizeLangDetector().loadModels();
  }

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage enrichedMsg = new EnrichedMessage(context.element());
    LOG.debug("Start DetectLanguageFn with message ID: {}", enrichedMsg.getId());

    /**
     * Optimaized Language detection library's bug.
     * https://github.com/optimaize/language-detector/issues/90
     */
    final String body = enrichedMsg.getContent().toLowerCase();
    final String languageCode = detector.detect(body).getLanguage();
    if (Language.isValidLang(languageCode)) {
      enrichedMsg.setLang(languageCode);
      context.output(enrichedMsg);
      return;
    }

    LOG.debug("[DROP-MSG] Cannot detect language for msg: {}", enrichedMsg.getId());
  }
}
