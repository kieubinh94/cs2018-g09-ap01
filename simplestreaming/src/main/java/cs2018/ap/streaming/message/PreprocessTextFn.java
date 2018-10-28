package cs2018.ap.streaming.message;

import cs2018.ap.streaming.lang.SimpleTokenizer;
import cs2018.ap.streaming.lang.Tokenizer;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* For removing unexpected characters from the message content */
@SuppressWarnings({
  "PMD.LongVariable",
  "PMD.BeanMembersShouldSerialize",
  "PMD.UncommentedEmptyConstructor"
})
public class PreprocessTextFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 6106269076155338045L;

  private static final Logger LOG = LoggerFactory.getLogger(Tokenizer.class);

  private transient Tokenizer tokenizer;

  @Setup
  public void setUp() {
    this.tokenizer = new SimpleTokenizer();
  }

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage relMsg = new EnrichedMessage(context.element());
    LOG.debug("Start PreprocessTextFn with message ID: {}", relMsg.getId());

    relMsg.setContent(this.tokenizer.preProcess(relMsg.getContent()));

    context.output(relMsg);
  }
}
