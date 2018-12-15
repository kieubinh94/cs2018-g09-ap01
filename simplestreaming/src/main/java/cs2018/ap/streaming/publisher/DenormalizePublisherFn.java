package cs2018.ap.streaming.publisher;

import cs2018.ap.streaming.io.ElasticsearchClient;
import cs2018.ap.streaming.io.Tuple;
import cs2018.ap.streaming.io.TupleDao;
import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.message.Publisher;
import java.util.Optional;
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

  private transient TupleDao tupleDao;

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
    this.tupleDao =
        new TupleDao(
            ElasticsearchClient.getClient(String.format("%s:%s", host, port), cluster), index);
  }

  @ProcessElement
  public void processElement(final ProcessContext context) throws IllegalArgumentException {
    final EnrichedMessage enrichedMsg = new EnrichedMessage(context.element());
    LOG.debug("Start DenormalizePublisherFn with message ID: {}", enrichedMsg.getId());
    final Optional<Tuple> tuple =
        tupleDao.loadByKey(enrichedMsg.getPublisher().getPartnerId(), type);
    if (tuple.isPresent()) {
      enrichPublishedByFields(enrichedMsg, tuple.get());
      context.output(enrichedMsg);
      return;
    }

    LOG.debug(
        String.format(
            "[DROP-MSG] Cannot denormalize publisher with id: %s, key: %s",
            enrichedMsg.getId(), enrichedMsg.getPublisher().getPartnerId()));
  }

  private void enrichPublishedByFields(final EnrichedMessage relMsg, final Tuple snsAccount) {
    final Publisher publishedBy = relMsg.getPublisher();
    publishedBy.setDisplayName(snsAccount.getAsNullableString("display_name"));
    publishedBy.setScreenName(snsAccount.getAsNullableString("screen_name"));
    publishedBy.setPartnerId(snsAccount.getAsNullableString("partner_id"));
    publishedBy.setAvatarUrl(snsAccount.getAsNullableString("avatar_url"));
    publishedBy.setStatus(snsAccount.getAsNullableInt("status", 0));
    publishedBy.setCountryCode(snsAccount.getAsNullableString("country_code"));
  }

  @Teardown
  public void close() {}
}
