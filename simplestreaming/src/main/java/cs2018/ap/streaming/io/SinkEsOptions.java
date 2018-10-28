package cs2018.ap.streaming.io;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface SinkEsOptions extends PipelineOptions {
  @Description("Elasticsearch cluster name")
  String getEsClusterName();

  void setEsClusterName(String clusterName);

  @Description("Elasticsearch host name")
  String getEsHostName();

  void setEsHostName(String hostName);

  @Description("Elasticsearch port")
  @Default.Integer(9300)
  int getEsPort();

  void setEsPort(int port);

  @Description("Elasticsearch index")
  String getEsIndex();

  void setEsIndex(String index);

  @Description("Elasticsearch document type")
  String getEsType();

  void setEsType(String type);

  @Default.Integer(1000)
  int getBatchSize();

  void setBatchSize(int size);
}
