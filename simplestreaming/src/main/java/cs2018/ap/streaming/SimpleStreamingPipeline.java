package cs2018.ap.streaming;

import com.google.common.collect.ImmutableMap;
import cs2018.ap.streaming.io.*;
import cs2018.ap.streaming.pipeline.SimplePipeline;
import java.io.IOException;
import java.util.Arrays;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.options.DataflowWorkerLoggingOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.LongVariable")
public final class SimpleStreamingPipeline extends StreamingRunOptions {
  private static final Logger LOG = LoggerFactory.getLogger(SimpleStreamingPipeline.class);

  public static void main(final String[] args) throws IOException {
    final SimpleStreamingPipeline main = new SimpleStreamingPipeline();
    final String[] beamOptions = new SimpleBeamOptionsBuilder().loadOptions(args, main);
    LOG.info("Deploy with options {}", Arrays.toString(beamOptions));

    main.build(beamOptions).run();
  }

  private Pipeline build(final String[] beamOptions) {
    final SimplePipeline simplePipeline = new SimplePipeline();
    simplePipeline.build(beamOptions);
    return simplePipeline.getResult();
  }

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public interface Options
      extends PubsubOptions,
          SinkEsOptions,
          PublisherEsOptions,
          TopicEsOptions,
          RedisOptions,
          DataflowWorkerLoggingOptions,
          DataflowPipelineOptions,
          SourceOptions {

    @Description("The version of current deployed")
    void setVersion(String value);

    String getVersion();

    String getLogLevel();

    void setLogLevel(String logLevel);
  }
}
