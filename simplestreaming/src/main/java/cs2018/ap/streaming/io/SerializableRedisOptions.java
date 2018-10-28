package cs2018.ap.streaming.io;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings(value = {"PMD.ShortMethodName"})
public class SerializableRedisOptions implements Serializable {

  private static final long serialVersionUID = 2849824205570256576L;

  private final int redisMaxConnect;
  private final String redisHost;
  private final int redisPort;

  public SerializableRedisOptions(
      final int redisMaxConnect, final String redisHost, final int redisPort) {
    this.redisMaxConnect = redisMaxConnect;
    this.redisHost = redisHost;
    this.redisPort = redisPort;
  }

  public static SerializableRedisOptions of(final RedisOptions pplRedisOpts) {
    return new SerializableRedisOptions(
        pplRedisOpts.getRedisMaxConnect(),
        pplRedisOpts.getRedisHost(),
        pplRedisOpts.getRedisPort());
  }

  public int getRedisMaxConnect() {
    return redisMaxConnect;
  }

  public String getRedisHost() {
    return redisHost;
  }

  public int getRedisPort() {
    return redisPort;
  }

  @Override
  public String toString() {
    return "SerializableRedisOptions{"
        + "redisMaxConnect="
        + redisMaxConnect
        + ", redisHost='"
        + redisHost
        + '\''
        + ", redisPort="
        + redisPort
        + '}';
  }

  @Override
  public boolean equals(final Object compareObject) {
    if (this == compareObject) {
      return true;
    }
    if (compareObject == null || getClass() != compareObject.getClass()) {
      return false;
    }
    final SerializableRedisOptions that = (SerializableRedisOptions) compareObject;
    return redisMaxConnect == that.redisMaxConnect
        && redisPort == that.redisPort
        && Objects.equals(redisHost, that.redisHost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(redisMaxConnect, redisHost, redisPort);
  }
}
