package cs2018.ap.streaming.pipeline;

import com.google.common.collect.ImmutableMap;
import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.*;
import cs2018.ap.streaming.message.EnrichedMessage;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;
import org.apache.logging.log4j.util.Strings;

public class SimplePipeline extends AbstractPipeline<Pipeline> {
  private Pipeline pipeline;

  public void build(final String[] options) {
    final SimpleStreamingPipeline.OptionsSink pplOptions =
        PipelineOptionsFactory.fromArgs(options)
            .withValidation()
            .as(SimpleStreamingPipeline.OptionsSink.class);

    pipeline = Pipeline.create(pplOptions);

    final SerializableRedisOptions redisOptions =
        new SerializableRedisOptions(
            pplOptions.getRedisMaxConnect(), pplOptions.getRedisHost(), pplOptions.getRedisPort());

    final PCollection<String> rawMessages =
        SourcePipelineBuilder.of()
            .setSources(
                ImmutableMap.of(
                    "TWITTER-MSGS",
                    PubsubSource.of(pplOptions.getProject(), pplOptions.getTwPubsubSubscription()),
                    "RSS-MSGS",
                    PubsubSource.of(
                        pplOptions.getProject(), pplOptions.getRssPubsubSubscription())))
            .loadData(pipeline, Strings.EMPTY)
            .getOutput();

    final PCollection<EnrichedMessage> sentimentMessages =
        ServicePipelineBuilder.of()
            .setSource(rawMessages)
            .transform(pplOptions, redisOptions)
            .getOutput();

    final PCollection<EnrichedMessage> enrichedMessages =
        EnrichPipelineBuilder.of()
            .setSource(sentimentMessages)
            .loadData(pipeline, "READ SENTIMENT MSG")
            .transform(pplOptions, redisOptions)
            .getOutput();

    SinkPipelineBuilder.of()
        .setSource(enrichedMessages)
        .setDestination(
            EsConfiguration.create(
                pplOptions.getEsHostName(),
                pplOptions.getEsPort(),
                pplOptions.getEsClusterName(),
                pplOptions.getEsIndex(),
                pplOptions.getEsType()))
        .loadData(pipeline, "READ ENRICHED MSGS")
        .transform(pplOptions, redisOptions);
  }

  @Override
  public Pipeline getResult() {
    return pipeline;
  }
}
