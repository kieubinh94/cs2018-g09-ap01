package cs2018.ap.streaming.io;

import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.LongVariable", "PMD.ImmutableField", "PMD.TooManyFields"})
public class StreamingRunOptions implements RunOptions {
  private static final Logger LOG = LoggerFactory.getLogger(StreamingRunOptions.class);

  @Parameter(
    description = "Sink environment. Available options: [qa, prod]",
    names = {"-p", "--sink"}
  )
  private transient String sink;

  @Parameter(names = {"-j", "--jobName"})
  private transient String jobName;

  @Parameter(names = {"-w", "--workerMachineType"})
  private transient String workerMachineType;

  @Parameter(
    description = "Pubsub to read message",
    names = {"-namedentity", "--namedentity"}
  )
  private transient String pubsub;

  @Parameter(
    description = "PubsubSubscription to read message",
    names = {"-sub", "--sub"}
  )
  private transient String pubsubSubscription;

  @Override
  public String getJobName() {
    return jobName;
  }

  public String getWorkerMachineType() {
    return workerMachineType;
  }

  @Override
  public List<String> getConfigFiles() {
    return ImmutableList.of("application.yml", "pubsub.yml", "sink.yml");
  }
}
