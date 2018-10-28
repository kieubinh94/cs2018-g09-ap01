package cs2018.ap.streaming.io;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface PublisherEsOptions extends PipelineOptions {

  @Description("Elasticsearch host name")
  String getPubEsHost();

  void setPubEsHost(String hostName);

  @Description("Elasticsearch port for REST API")
  @Default.Integer(9300)
  int getPubEsPort();

  void setPubEsPort(int port);

  @Description("Elasticsearch cluster")
  String getPubEsCluster();

  void setPubEsCluster(String index);

  @Description("Elasticsearch index")
  String getPubEsIndex();

  void setPubEsIndex(String index);

  @Description("Elasticsearch type")
  String getPubEsType();

  void setPubEsType(String type);
}
