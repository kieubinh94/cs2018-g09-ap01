package cs2018.ap.streaming.io;

import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.LongVariable", "PMD.ImmutableField", "PMD.TooManyFields"})
public class StreamingRunOptions implements RunOptions {
  private static final Logger LOG = LoggerFactory.getLogger(StreamingRunOptions.class);

  @Parameter(
    description = "Running environment. Available options: [qa, prod]",
    names = {"-e", "--env"},
    required = true
  )
  private transient String env;

  @Parameter(
    description = "Sink environment. Available options: [qa, prod]",
    names = {"-p", "--sink"}
  )
  private transient String sink;

  @Parameter(
    description = "Running user.",
    names = {"-u", "--user"}
  )
  private transient String user;

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
  public String getEnv() {
    return env;
  }

  @Override
  public String getUser() {
    return user;
  }

  @Override
  public String getSink() {
    return sink;
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  public String getWorkerMachineType() {
    return workerMachineType;
  }

  @Override
  public List<String> getCommonConfigFiles() {
    return ImmutableList.of("application.yml", "pubsub.yml");
  }

  @Override
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public Map<String, String> getEnvConfigFileMap() {
    final String sinkEnv = StringUtils.isBlank(getSink()) ? getEnv() : getSink();

    LOG.info("INPUT ARGUMENTS:");
    LOG.info(" - {}: {}", "GCLOUD PROJECT ENV", getEnv());
    LOG.info(" - {}: {}", "SINK ENV", sinkEnv);

    return ImmutableMap.of("application", getEnv(), "sink", sinkEnv);
  }
}
