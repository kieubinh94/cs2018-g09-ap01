package cs2018.ap.streaming.namedentity;

import avro.shaded.com.google.common.base.Preconditions;
import cs2018.ap.streaming.io.ElasticsearchClient;
import cs2018.ap.streaming.io.TupleDao;
import cs2018.ap.streaming.io.Tuple;
import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.message.NamedEntity;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "PMD.LongVariable"})
public class DenormalizeNamedEntityFn extends DoFn<EnrichedMessage, EnrichedMessage> {
  private static final long serialVersionUID = 5262996782113981453L;
  private static final Logger LOG = LoggerFactory.getLogger(DenormalizeNamedEntityFn.class);

  private transient TupleDao tupleDao;

  private final String cluster;
  private final String host;
  private final int port;
  private final String index;
  private final String type;

  public DenormalizeNamedEntityFn(
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
  public void processElement(final ProcessContext context) {
    final EnrichedMessage originalMsg = context.element();
    LOG.debug("Start DenormalizeNamedEntityFn with message ID: {}", originalMsg.getId());

    Preconditions.checkNotNull(
        originalMsg.getPublisher(),
        "Missed publishedBy in relevant message. We need publishedBy to find score_topic for ne_mentions");

    final EnrichedMessage msg = new EnrichedMessage(originalMsg);
    msg.setDenormalizedNamedEntities(
        buildDenormalizedNes(
            originalMsg
                .getDenormalizedNamedEntities()
                .stream()
                .map(NamedEntity::getId)
                .collect(Collectors.toSet())));

    context.output(msg);
  }

  private List<NamedEntity> buildDenormalizedNes(final Set<Integer> tagIds) {
    if (tagIds.isEmpty()) {
      return Collections.emptyList();
    }

    return tagIds
        .stream()
        .map(
            tagId -> {
              final Tuple tuple = tupleDao.loadByKey(String.valueOf(tagId), type).get();
              final NamedEntity topic = new NamedEntity();
              topic.setId(tagId);
              topic.setName(tuple.getAsNullableString("name"));
              topic.setCountryCode(tuple.getAsNullableString("country_code"));
              return topic;
            })
        .collect(Collectors.toList());
  }
}
