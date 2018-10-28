package cs2018.ap.streaming.pipeline;

import cs2018.ap.streaming.io.*;
import java.io.Serializable;
import java.util.Map;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.AfterPane;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

@SuppressWarnings("PMD.AbstractNaming")
public abstract class PipelineBuilder<InputType, OutputType, Options extends SourceOptions>
    implements Serializable {
  private static final long serialVersionUID = -8248321484973419436L;

  ///////////////////////////////////
  // INPUT
  ///////////////////////////////////
  transient String inputFile;
  transient PubsubSource pubsubSource;
  transient Map<String, PubsubSource> pubsubSources;
  transient PCollection<InputType> input;

  ///////////////////////////////////
  // OUTPUT
  ///////////////////////////////////
  transient String outputFile;
  transient int numberOfFiles;
  transient EsConfiguration esConf;
  transient PCollection<OutputType> output;

  PipelineBuilder<InputType, OutputType, Options> setSource(final String inputFile) {
    this.inputFile = inputFile;
    return this;
  }

  PipelineBuilder<InputType, OutputType, Options> setSource(final PCollection<InputType> input) {
    this.input = input;
    return this;
  }

  public PipelineBuilder<InputType, OutputType, Options> setSource(final PubsubSource from) {
    this.pubsubSource = from;
    return this;
  }

  PipelineBuilder<InputType, OutputType, Options> setSources(final Map<String, PubsubSource> from) {
    this.pubsubSources = from;
    return this;
  }

  public PipelineBuilder<InputType, OutputType, Options> setDestination(
      final EsConfiguration esConf) {
    this.esConf = esConf;
    return this;
  }

  PipelineBuilder<InputType, OutputType, Options> setDestination(
      final String outputFile, final int numberOfFiles) {
    this.outputFile = outputFile;
    this.numberOfFiles = numberOfFiles;
    return this;
  }

  public PipelineBuilder<InputType, OutputType, Options> loadData(
      final Pipeline pipeline, final String readName) {
    throw new UnsupportedOperationException();
  }

  public abstract PipelineBuilder<InputType, OutputType, Options> transform(
      Options pplOptions, SerializableRedisOptions redisOptions);

  public PCollection<OutputType> getOutput() {
    return output;
  }

  String parDoNameOf(final String name) {
    return String.format("ParDo(%s)", name);
  }

  <T> void sinkToFile(
      final PCollection<T> input, DoFn<T, String> toStringFn, final Options options) {
    if (options.isUnbounded()) {
      input
          .apply(
              Window.<T>into(FixedWindows.of(Duration.standardSeconds(5)))
                  .discardingFiredPanes()
                  .triggering(AfterPane.elementCountAtLeast(1))
                  .withAllowedLateness(Duration.standardSeconds(0)))
          .apply(ParDo.of(toStringFn))
          .apply(getFileWriter(options).to(outputFile));
    } else {
      input.apply(ParDo.of(toStringFn)).apply(getFileWriter(options).to(outputFile));
    }
  }

  private TextIO.Write getFileWriter(final SourceOptions options) {
    TextIO.Write write = TextIO.write();
    if (options.isUnbounded()) {
      write = write.withNumShards(numberOfFiles).withWindowedWrites(); // write to 1000 files
    }
    return write;
  }
}
