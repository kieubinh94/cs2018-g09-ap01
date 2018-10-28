package cs2018.ap.streaming.io;

import com.beust.jcommander.JCommander;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import cs2018.ap.streaming.utils.PropertiesUtils;
import cs2018.ap.streaming.utils.StringConstants;
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

    Preconditions.checkArgument(
        "qa".equalsIgnoreCase(runOptions.getEnv()) || "prod".equalsIgnoreCase(runOptions.getEnv()),
        String.format(
            "Wrong environment '%s', please try with [%s]", runOptions.getEnv(), "[qa|prod]"));

    final List<String> configPaths = new ArrayList<>(runOptions.getCommonConfigFiles());

    final Map<String, String> envConfigFileMap = runOptions.getEnvConfigFileMap();
    envConfigFileMap.forEach(
        (prefix, env) -> configPaths.add(String.format("%s-%s.yml", prefix, env)));

    configPaths.addAll(getExtraFiles(runOptions));

    return PropertiesUtils.loadResourceConfig(configPaths);
  }

  List<String> getExtraFiles(final R runOptions) {
    return ImmutableList.of();
  }

  String addJobIdentifier(final Map<String, Object> options, final R runOptions) {
    final String version = PropertiesUtils.getMessagePipelineVersion();
    options.put(VERSION_OPTION, version);

    String jobName;
    if (StringUtils.isNotBlank(runOptions.getJobName())) {
      jobName = runOptions.getJobName();
    } else {
      final String prefixJobName = options.get(JOB_NAME).toString();
      jobName = prefixJobName;
      if (!StringUtils.isEmpty(runOptions.getUser())) {
        final String dfVersion = StringUtils.replaceAll(version, "\\.", StringConstants.HYPHEN);
        jobName = String.format("%s-%s-%s", prefixJobName, runOptions.getUser(), dfVersion);
      }
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
    if (PipelineUtils.isJobRunning(project, region, jobName)) {
      final String[] newOptions = Arrays.copyOf(beamOptions, beamOptions.length + 1);
      newOptions[beamOptions.length] = "--update";
      return newOptions;
    }
    return beamOptions;
  }
}
