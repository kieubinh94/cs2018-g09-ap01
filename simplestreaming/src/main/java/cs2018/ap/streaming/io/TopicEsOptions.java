package cs2018.ap.streaming.io;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface TopicEsOptions extends PipelineOptions {

  @Description("Elasticsearch host name")
  String getTopicEsHost();

  void setTopicEsHost(String hostName);

  @Description("Elasticsearch port for REST API")
  @Default.Integer(9300)
  int getTopicEsPort();

  void setTopicEsPort(int port);

  @Description("Elasticsearch cluster")
  String getTopicEsCluster();

  void setTopicEsCluster(String index);

  @Description("Elasticsearch index")
  String getTopicEsIndex();

  void setTopicEsIndex(String index);

  @Description("Elasticsearch type")
  String getTopicEsType();

  void setTopicEsType(String type);
}
