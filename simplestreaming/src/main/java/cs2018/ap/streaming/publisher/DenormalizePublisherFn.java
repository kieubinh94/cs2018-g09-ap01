package cs2018.ap.streaming.publisher;

import cs2018.ap.streaming.io.PublisherDao;
import cs2018.ap.streaming.message.EnrichedMessage;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class DenormalizePublisherFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 8622271807854554909L;
  private static final Logger LOG = LoggerFactory.getLogger(DenormalizePublisherFn.class);

  private final String cluster;
  private final String host;
  private final int port;
  private final String index;
  private final String type;

  private transient PublisherDao publisherDao;

  public DenormalizePublisherFn(
      final String cluster,
      final String host,
      final int port,
      final String index,
      final String type) {
    this.cluster = cluster;
    this.host = host;
    this.port = port;
    this.index = index;
    this.type = type;
  }

  @Setup
  public void setUp() {
    LOG.debug("Connecting to {}:{}/{}", host, port, index);
    /*publisherDao =
    new PublisherDao(
        ElasticsearchClient.getClient(String.format("%s:%s", host, port), cluster), index);*/
  }

  @ProcessElement
  public void processElement(final ProcessContext context) throws IllegalArgumentException {
    /*final EnrichedMessage enrichedMsg = new EnrichedMessage(context.element());
    LOG.debug("Start DenormalizePublisherFn with message ID: {}", enrichedMsg.getId());
    final String key = String.format("%s:%s", enrichedMsg.getPublisher().getChannel(), enrichedMsg.getPublisher().getPartnerId());

    final Optional<Tuple> snsAccount = publisherDao.loadByKey(publisherKey, type);
    if (snsAccount.isPresent()) {
      RelevantMessageBuilder.PublishedByBuilder.enrichPublishedByFields(enrichedMsg, snsAccount.get());
      context.output(enrichedMsg);
      return;
    }

    throw new IllegalStateException(
        String.format(
            "[DROP-MSG] Cannot denormalize publisher with id: %s, key: %s",
            enrichedMsg.getId(), key));*/
  }

  @Teardown
  public void close() {}
}
