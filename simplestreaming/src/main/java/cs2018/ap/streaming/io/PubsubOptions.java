package cs2018.ap.streaming.io;

import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.Description;

/** OptionsSink that can be used to configure Pub/Sub namedentity in Beam examples. */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface PubsubOptions extends GcpOptions {
  /////////////////////////////////////////////////////////
  // subscription to read messages
  /////////////////////////////////////////////////////////
  @Description("Pub/Sub Twitter subscription")
  String getTwPubsubSubscription();

  @SuppressWarnings("unused")
  void setTwPubsubSubscription(String subscription);

  @Description("Pub/Sub Blog subscription")
  String getRssPubsubSubscription();

  @SuppressWarnings("unused")
  void setRssPubsubSubscription(String subscription);
}
