package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.message.*;
import cs2018.ap.streaming.publisher.MatchPublisherFn;
import org.apache.beam.sdk.transforms.ParDo;

@SuppressWarnings({"PMD.ShortMethodName"})
public class ServicePipelineBuilder
    extends PipelineBuilder<String, EnrichedMessage, SimpleStreamingPipeline.OptionsSink> {

  public static PipelineBuilder<String, EnrichedMessage, SimpleStreamingPipeline.OptionsSink> of() {
    return new ServicePipelineBuilder();
  }

  @Override
  public PipelineBuilder<String, EnrichedMessage, SimpleStreamingPipeline.OptionsSink> transform(
      final SimpleStreamingPipeline.OptionsSink pplOptions,
      final SerializableRedisOptions redisOptions) {
    output =
        input
            .apply(ParDo.of(new ReadJsonToRawMessageFn()))
            .apply(ParDo.of(new ConvertRawToRelFn()))
            .apply(ParDo.of(new MatchPublisherFn(redisOptions)))
            .apply(ParDo.of(new PreprocessTextFn()))
            .apply(ParDo.of(new DetectBlacklistFn()));
    return this;
  }
}
