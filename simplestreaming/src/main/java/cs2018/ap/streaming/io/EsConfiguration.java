package cs2018.ap.streaming.io;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A POJO describing a connection configuration to Elasticsearch. */
@SuppressWarnings("PMD.AbstractNaming")
@AutoValue
public abstract class EsConfiguration implements Serializable {

  private static final Logger LOG = LoggerFactory.getLogger(EsConfiguration.class);

  public static EsConfiguration create(
      final String hostName,
      final int port,
      final String clusterName,
      final String index,
      final String type) {
    return create(hostName, port, clusterName, index, type, false);
  }

  /**
   * Creates a new Elasticsearch connection configuration.
   *
   * @param index the index toward which the requests will be issued
   * @param type the document type toward which the requests will be issued
   * @return the connection configuration object
   */
  public static EsConfiguration create(
      final String hostName,
      final int port,
      final String clusterName,
      final String index,
      final String type,
      final boolean staticIndex) {
    checkArguments(hostName, port, clusterName, index, type);
    return new AutoValue_EsConfiguration.Builder()
        .setHostName(hostName)
        .setPort(port)
        .setClusterName(clusterName)
        .setIndex(index)
        .setType(type)
        .build();
  }

  private static void checkArguments(
      final String hostName,
      final int port,
      final String clusterName,
      final String index,
      final String type) {
    checkArgument(hostName != null, "EsConfiguration.create(...) called with null hostName");
    checkArgument(port > 0, "EsConfiguration.create(...) called with invalid port");
    checkArgument(clusterName != null, "EsConfiguration.create(...) called with null clusterName");
    checkArgument(index != null, "EsConfiguration.create(...) called with null index");
    checkArgument(type != null, "EsConfiguration.create(...) called with null type");
  }

  public abstract String getHostName();

  public abstract int getPort();

  public abstract String getClusterName();

  @Nullable
  public abstract String getConfigPrefix();

  @Nullable
  public abstract String getIndex();

  @Nullable
  public abstract List<String> getAlias();

  public abstract String getType();

  abstract Builder builder();

  @SuppressWarnings("PMD.AbstractNaming")
  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder setHostName(String hostName);

    abstract Builder setPort(int port);

    abstract Builder setClusterName(String clusterName);

    abstract Builder setConfigPrefix(String prefix);

    abstract Builder setIndex(String index);

    abstract Builder setAlias(List<String> alias);

    abstract Builder setType(String type);

    abstract EsConfiguration build();
  }
}
