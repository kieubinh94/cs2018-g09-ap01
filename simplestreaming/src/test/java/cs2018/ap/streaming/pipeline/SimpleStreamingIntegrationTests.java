package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.SimpleStreamingPipeline;
import cs2018.ap.streaming.io.EsConfiguration;
import cs2018.ap.streaming.io.SerializableRedisOptions;
import cs2018.ap.streaming.io.SimpleBeamOptionsBuilder;
import cs2018.ap.streaming.message.EnrichedMessage;
import java.io.IOException;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.logging.log4j.util.Strings;
import org.junit.Rule;
import org.junit.Test;

public class SimpleStreamingIntegrationTests {
  @Rule public transient TestPipeline pipeline = TestPipeline.create();

  @Test
  public void given_msgFile_when_doStreaming_then_hasDataInEs() throws IOException {
    // GIVEN
    final String[] beamOptions =
        new SimpleBeamOptionsBuilder().loadOptions(new String[0], new SimpleStreamingPipeline());
    final SimpleStreamingPipeline.Options pplOptions =
        PipelineOptionsFactory.fromArgs(beamOptions)
            .withValidation()
            .as(SimpleStreamingPipeline.Options.class);

    // WHEN
    final SerializableRedisOptions redisOptions =
        new SerializableRedisOptions(
            pplOptions.getRedisMaxConnect(), pplOptions.getRedisHost(), pplOptions.getRedisPort());

    final PCollection<String> rawMessages =
        SourcePipelineBuilder.of()
            .setSource("src/test/resources/demo-msg.json")
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
        .transform(pplOptions, redisOptions);

    // THEN
    pipeline.run().waitUntilFinish();
  }
}
