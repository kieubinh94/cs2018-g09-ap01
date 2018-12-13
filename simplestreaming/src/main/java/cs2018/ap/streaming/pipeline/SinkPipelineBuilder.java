package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.EsIO;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.io.ToRelJsonFn;
import cs2018.ap.streaming.message.EnrichedMessage;
import cs2018.ap.streaming.message.ToEsRelDocFn;
import java.util.Objects;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.ShortMethodName"})
public final class SinkPipelineBuilder
    extends PipelineBuilder<EnrichedMessage, Void, SimpleStreamingPipeline.Options> {
  private static final Logger LOG = LoggerFactory.getLogger(SinkPipelineBuilder.class);

  public static PipelineBuilder<EnrichedMessage, Void, SimpleStreamingPipeline.Options> of() {
    return new SinkPipelineBuilder();
  }

  @Override
  public PipelineBuilder<EnrichedMessage, Void, SimpleStreamingPipeline.Options> transform(
      final SimpleStreamingPipeline.Options pplOptions,
      final SerializableRedisOptions redisOptions) {

    if (Objects.nonNull(outputFile)) {
      input.apply(ParDo.of(new ToRelJsonFn())).apply(TextIO.write().to(outputFile));
    }
    if (Objects.nonNull(esConf)) {
      input
          .apply("TO-ES-DOC", ParDo.of(new ToEsRelDocFn()))
          .apply(String.format("SINK: %s", esConf.getIndex()), EsIO.write().withConfig(esConf));
    }
    return this;
  }
}
