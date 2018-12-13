package cs2018.ap.streaming.io;

import cs2018.ap.streaming.utils.StringConstants;
import java.io.Serializable;
import org.apache.beam.sdk.repackaged.org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public final class ElasticsearchClient extends TransportClient implements Serializable {
  private static final long serialVersionUID = 429601320975716888L;
  private static final String DEFAULT_PORT = "9300";

  private ElasticsearchClient() {}

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public static Client getClient(final String clusterNodes, final String clusterName) {
    final TransportClient client = new TransportClient(settings(clusterName));
    for (final String clusterNode : StringUtils.split(clusterNodes, StringConstants.COMMA)) {
      final String hostName = StringUtils.substringBefore(clusterNode, StringConstants.COLON);
      String port = StringUtils.substringAfter(clusterNode, StringConstants.COLON);
      if (port.isEmpty()) {
        port = DEFAULT_PORT;
      }
      client.addTransportAddress(new InetSocketTransportAddress(hostName, Integer.parseInt(port)));
    }
    return client;
  }

  private static Settings settings(final String clusterName) {
    return ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
  }
}
