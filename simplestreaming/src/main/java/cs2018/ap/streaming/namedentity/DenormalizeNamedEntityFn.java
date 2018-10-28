package cs2018.ap.streaming.namedentity;

import avro.shaded.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.message.NamedEntity;
import java.util.ArrayList;
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
  public static final String REDIS_KEY_TMPL = "namedentity:named_entities:%s";

  @ProcessElement
  public void processElement(final ProcessContext context) {
    final EnrichedMessage originalMsg = context.element();
    LOG.debug("Start DenormalizeNamedEntityFn with message ID: {}", originalMsg.getId());

    Preconditions.checkNotNull(
        originalMsg.getPublisher(),
        "Missed publishedBy in relevant message. We need publishedBy to find score_topic for ne_mentions");

    final EnrichedMessage relMsg = new EnrichedMessage(originalMsg);
    relMsg.setDenormalizedNamedEntities(
        buildDenormalizedNes(
            originalMsg
                .getDenormalizedNamedEntities()
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toSet())));

    context.output(relMsg);
  }

  private List<NamedEntity> buildDenormalizedNes(final Set<Integer> tagIds) {
    if (tagIds.isEmpty()) {
      return Collections.emptyList();
    }

    // final List<Integer> neIds = new ArrayList<>(tagIds);
    final List<NamedEntity> namedEntities = ImmutableList.of();
    return new ArrayList<>(namedEntities);
  }
}
