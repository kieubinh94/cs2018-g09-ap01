package cs2018.ap.streaming.io;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface RedisOptions extends PipelineOptions {

  @Description("Maximum of number io")
  @Default.Integer(1000)
  int getRedisMaxConnect();

  void setRedisMaxConnect(int redisMaxConnect);

  String getRedisHost();

  void setRedisHost(String redisHost);

  @Default.Integer(6379)
  int getRedisPort();

  void setRedisPort(int redisPort);
}
