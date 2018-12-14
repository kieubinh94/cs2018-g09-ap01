package cs2018.ap.streaming.pipeline;

import com.google.common.collect.ImmutableMap;
import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.*;
import cs2018.ap.streaming.message.EnrichedMessage;
import org.apache.beam.runners.dataflow.options.DataflowWorkerLoggingOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;
import org.apache.logging.log4j.util.Strings;

public class SimplePipeline extends AbstractPipeline<Pipeline> {
  private Pipeline pipeline;

  public void build(final String[] options) {
    final SimpleStreamingPipeline.Options pplOptions =
        PipelineOptionsFactory.fromArgs(options)
            .withValidation()
            .as(SimpleStreamingPipeline.Options.class);

    pipeline = Pipeline.create(pplOptions);
    setLogLevel(pplOptions);

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

    final PCollection<EnrichedMessage> nerMsgs =
        ServicePipelineBuilder.of()
            .setSource(rawMessages)
            .transform(pplOptions, redisOptions)
            .getOutput();

    final PCollection<EnrichedMessage> finalMsgs =
        EnrichPipelineBuilder.of()
            .setSource(nerMsgs)
            .transform(pplOptions, redisOptions)
            .getOutput();

    SinkPipelineBuilder.of()
        .setSource(finalMsgs)
        .setDestination(
            EsConfiguration.create(
                pplOptions.getEsHostName(),
                pplOptions.getEsPort(),
                pplOptions.getEsClusterName(),
                pplOptions.getEsIndex(),
                pplOptions.getEsType()))
        .transform(pplOptions, redisOptions);
  }

  @Override
  public Pipeline getResult() {
    return pipeline;
  }

  private void setLogLevel(final SimpleStreamingPipeline.Options pplOptions) {
    final String level = pplOptions.getLogLevel();
    pplOptions.setWorkerLogLevelOverrides(
        DataflowWorkerLoggingOptions.WorkerLogLevelOverrides.from(
            ImmutableMap.of("cs2018.ap.streaming", level)));
  }
}
