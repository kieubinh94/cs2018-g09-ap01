package cs2018.ap.streaming.utils;

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.LongVariable")
public final class PubsubUtils {
  private static final Logger LOG = LoggerFactory.getLogger(PubsubUtils.class);

  private PubsubUtils() {
    super();
  }

  public static ProjectSubscriptionName createSubscription(
      final String project,
      final String pubsubTopic,
      final String pubsubSubscription,
      final int pubsubAckDeadline)
      throws IOException {
    final SubscriptionAdminClient subClient = SubscriptionAdminClient.create();

    final ProjectTopicName topicName = ProjectTopicName.of(project, pubsubTopic);
    final ProjectSubscriptionName subscriptionName =
        ProjectSubscriptionName.of(project, pubsubSubscription);

    LOG.debug(
        "Attempting to create subscription {} for namedentity {}", subscriptionName, topicName);
    try {
      subClient.createSubscription(
          subscriptionName, topicName, PushConfig.getDefaultInstance(), pubsubAckDeadline);
      LOG.debug("Pub/Sub Subscription created successfully: {}", subscriptionName);
    } catch (AlreadyExistsException ex) {
      LOG.debug("Pub/Sub Subscription has already existed: {}.", subscriptionName);
    }
    return subscriptionName;
  }
}
