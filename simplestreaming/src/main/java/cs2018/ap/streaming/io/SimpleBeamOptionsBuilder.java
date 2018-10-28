package cs2018.ap.streaming.io;

import static cs2018.ap.streaming.io.RunOptions.ENABLE;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.LongVariable", "PMD.AvoidDuplicateLiterals"})
public final class SimpleBeamOptionsBuilder extends BeamOptionsBuilder<StreamingRunOptions> {
  private static final Logger LOG = LoggerFactory.getLogger(StreamingRunOptions.class);

  public String[] loadOptions(final String[] args, final StreamingRunOptions runOptions)
      throws IOException {
    final Map<String, Object> ymlOptions = this.loadYml(args, runOptions);
    if (Objects.nonNull(runOptions.getWorkerMachineType())) {
      ymlOptions.put("workerMachineType", runOptions.getWorkerMachineType());
    }

    final String jobName = this.addJobIdentifier(ymlOptions, runOptions);
    final String[] beamOptions = this.toBeamStyleOptions(ymlOptions);
    final String projectName = ymlOptions.get("project").toString();
    final String region = ymlOptions.get("region").toString();
    return this.setUpdateOptIfJobExisted(beamOptions, projectName, region, jobName);
  }

  private boolean isEnable(final int option) {
    return option == ENABLE;
  }
}
