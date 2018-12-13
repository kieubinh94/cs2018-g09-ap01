package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.message.*;
import cs2018.ap.streaming.namedentity.*;
import cs2018.ap.streaming.publisher.DenormalizePublisherFn;
import cs2018.ap.streaming.publisher.PublisherFilters;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.ShortMethodName"})
public final class EnrichPipelineBuilder
    extends PipelineBuilder<EnrichedMessage, EnrichedMessage, SimpleStreamingPipeline.Options> {
  private static final Logger LOG = LoggerFactory.getLogger(EnrichPipelineBuilder.class);

  public static PipelineBuilder<EnrichedMessage, EnrichedMessage, SimpleStreamingPipeline.Options>
      of() {
    return new EnrichPipelineBuilder();
  }

  @Override
  public PipelineBuilder<EnrichedMessage, EnrichedMessage, SimpleStreamingPipeline.Options>
      transform(
          final SimpleStreamingPipeline.Options pplOptions,
          final SerializableRedisOptions redisOptions) {

    output =
        input
            .apply(
                parDoNameOf("FilterUnknownPublisherFn"),
                Filter.by(PublisherFilters.NOT_EMPTY_PUBLISHER))
            .apply(parDoNameOf("FilterBlacklistFn"), Filter.by(MessageFilters.NOT_BLACKLIST))
            .apply(
                parDoNameOf("FilterEmptyNamedEntity"),
                Filter.by(TopicFilters.NOT_EMPTY_NAMED_ENTITY))
            .apply(
                ParDo.of(
                    new DenormalizePublisherFn(
                        pplOptions.getPubEsCluster(),
                        pplOptions.getPubEsHost(),
                        pplOptions.getPubEsPort(),
                        pplOptions.getPubEsIndex(),
                        pplOptions.getPubEsType())))
            .apply(
                ParDo.of(
                    new DenormalizeNamedEntityFn(
                        pplOptions.getTopicEsCluster(),
                        pplOptions.getTopicEsHost(),
                        pplOptions.getTopicEsPort(),
                        pplOptions.getTopicEsIndex(),
                        pplOptions.getTopicEsType())));

    return this;
  }
}
