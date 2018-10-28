package cs2018.ap.streaming.io;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptions;

public interface SourceOptions extends PipelineOptions {
  @Default.Boolean(false)
  boolean isUnbounded();

  @SuppressWarnings("unused")
  void setUnbounded(boolean value);
}
