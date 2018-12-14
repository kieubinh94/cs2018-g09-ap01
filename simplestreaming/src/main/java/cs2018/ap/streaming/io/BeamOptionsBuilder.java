package cs2018.ap.streaming.io;

import com.beust.jcommander.JCommander;
import cs2018.ap.streaming.utils.PropertiesUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.AbstractNaming")
public abstract class BeamOptionsBuilder<R extends RunOptions> {
  private static final Logger LOG = LoggerFactory.getLogger(BeamOptionsBuilder.class);

  private static final String JOB_NAME = "jobName";
  private static final String VERSION_OPTION = "version";

  public abstract String[] loadOptions(final String[] args, final R runOptions) throws IOException;

  Map<String, Object> loadYml(final String[] mainArgs, final R runOptions) throws IOException {
    JCommander.newBuilder().addObject(runOptions).build().parse(mainArgs);

    final List<String> configPaths = new ArrayList<>(runOptions.getConfigFiles());
    return PropertiesUtils.loadResourceConfig(configPaths);
  }

  String addJobIdentifier(final Map<String, Object> options, final R runOptions) {
    final String version = PropertiesUtils.getMessagePipelineVersion();
    options.put(VERSION_OPTION, version);

    String jobName;
    if (StringUtils.isNotBlank(runOptions.getJobName())) {
      jobName = runOptions.getJobName();
    } else {
      jobName = options.get(JOB_NAME).toString();
      LOG.debug("Job name is {}", jobName);
      options.put(JOB_NAME, jobName);
    }
    options.put(JOB_NAME, jobName);
    return jobName;
  }

  String[] toBeamStyleOptions(final Map<String, Object> ymlOptions) {
    return ymlOptions
        .entrySet()
        .stream()
        .map(
            entry ->
                PropertiesUtils.toBeamParamStyle(entry.getKey(), String.valueOf(entry.getValue())))
        .toArray(String[]::new);
  }

  String[] setUpdateOptIfJobExisted(
      final String[] beamOptions, final String project, final String region, final String jobName)
      throws IOException {
    /*if (PipelineUtils.isJobRunning(project, region, jobName)) {
      final String[] newOptions = Arrays.copyOf(beamOptions, beamOptions.length + 1);
      newOptions[beamOptions.length] = "--update";
      return newOptions;
    }*/
    return beamOptions;
  }
}
