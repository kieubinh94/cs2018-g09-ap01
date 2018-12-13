package cs2018.ap.streaming.io;

import java.util.List;

@SuppressWarnings({"PMD.LongVariable", "PMD.ShortMethodName"})
public interface RunOptions {
  int ENABLE = 1;
  int DISABLE = 0;

  String getJobName();

  List<String> getConfigFiles();
}
