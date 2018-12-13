package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.PubsubSource;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.ShortMethodName"})
public final class SourcePipelineBuilder
    extends PipelineBuilder<Void, String, SimpleStreamingPipeline.Options> {
  private static final Logger LOG = LoggerFactory.getLogger(SourcePipelineBuilder.class);

  public static PipelineBuilder<Void, String, SimpleStreamingPipeline.Options> of() {
    return new SourcePipelineBuilder();
  }

  @Override
  public PipelineBuilder<Void, String, SimpleStreamingPipeline.Options> loadData(
      final Pipeline pipeline, final String readName) {

    if (Objects.nonNull(inputFile)) {
      output = pipeline.apply(TextIO.read().from(inputFile).withHintMatchesManyFiles());
    }
    if (Objects.nonNull(pubsubSources)) {
      final List<PCollection<String>> colList = new ArrayList<>();
      for (final Map.Entry<String, PubsubSource> entry : pubsubSources.entrySet()) {
        LOG.info("Reading from PubSub: {}", entry.getValue().getPubsubId());
        colList.add(
            pipeline.apply(
                entry.getKey(),
                PubsubIO.readStrings().fromSubscription(entry.getValue().getPubsubAddress())));
      }
      output = PCollectionList.of(colList).apply("MERGE MESSAGES", Flatten.pCollections());
    }
    return this;
  }

  @Override
  public PipelineBuilder<Void, String, SimpleStreamingPipeline.Options> transform(
      final SimpleStreamingPipeline.Options pplOptions,
      final SerializableRedisOptions redisOptions) {
    return this;
  }
}
