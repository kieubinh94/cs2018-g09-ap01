package cs2018.ap.streaming.io;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"PMD.LongVariable", "PMD.ShortMethodName"})
public interface RunOptions {
  int ENABLE = 1;
  int DISABLE = 0;

  String getEnv();

  String getUser();

  String getSink();

  String getJobName();

  List<String> getCommonConfigFiles();

  Map<String, String> getEnvConfigFileMap();
}
