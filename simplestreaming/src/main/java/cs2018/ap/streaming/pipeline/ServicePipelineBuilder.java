package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.message.*;
import cs2018.ap.streaming.namedentity.DetectNerFn;
import cs2018.ap.streaming.publisher.MatchPublisherFn;
import org.apache.beam.sdk.transforms.ParDo;

@SuppressWarnings({"PMD.ShortMethodName"})
public class ServicePipelineBuilder
    extends PipelineBuilder<String, EnrichedMessage, SimpleStreamingPipeline.Options> {

  public static PipelineBuilder<String, EnrichedMessage, SimpleStreamingPipeline.Options> of() {
    return new ServicePipelineBuilder();
  }

  @Override
  public PipelineBuilder<String, EnrichedMessage, SimpleStreamingPipeline.Options> transform(
      final SimpleStreamingPipeline.Options pplOptions,
      final SerializableRedisOptions redisOptions) {
    output =
        input
            .apply(ParDo.of(new ReadJsonToRawMessageFn()))
            .apply(ParDo.of(new ConvertRawToRelFn()))
            .apply(ParDo.of(new MatchPublisherFn(redisOptions)))
            .apply(ParDo.of(new PreprocessTextFn()))
            .apply(ParDo.of(new DetectBlacklistFn()))
            .apply(ParDo.of(new DetectNerFn()));
    return this;
  }
}
