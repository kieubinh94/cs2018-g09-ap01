package cs2018.ap.streaming.io;

import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.ListJobsResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.beam.runners.dataflow.DataflowClient;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PipelineUtils {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineUtils.class);

  private static final String RUNNING = "JOB_STATE_RUNNING";

  private PipelineUtils() {
    super();
  }

  public static boolean isJobRunning(
      final String project, final String region, final String jobName) throws IOException {
    final DataflowClient client =
        DataflowClient.create(buildDataflowPipelineOptions(project, region));
    final ListJobsResponse response = client.listJobs(null);
    final List<Job> jobs = response.getJobs();
    if (Objects.nonNull(jobs)) {
      for (final Job job : jobs) {
        if (job.getName().equalsIgnoreCase(jobName)
            && RUNNING.equalsIgnoreCase(job.getCurrentState())) {
          LOG.info("Found existing job '{}' running", jobName);
          return true;
        }
      }
    }
    return false;
  }

  private static DataflowPipelineOptions buildDataflowPipelineOptions(
      final String projectName, final String region) {
    return PipelineOptionsFactory.fromArgs("--project=" + projectName, "--region=" + region)
        .withValidation()
        .as(DataflowPipelineOptions.class);
  }
}
