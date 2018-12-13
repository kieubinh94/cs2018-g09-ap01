package cs2018.ap.crawler;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.ShortMethodName"})
public class PubsubClient {
  private static final Logger LOG = LoggerFactory.getLogger(PubsubClient.class);
  private final String projectId;

  public PubsubClient(final String projectId) {
    this.projectId = projectId;
  }

  public static PubsubClient of(final String projectId) {
    return new PubsubClient(projectId);
  }

  public ProjectTopicName createTopic(final String topicId) throws IOException {
    return createTopic(ProjectTopicName.of(projectId, topicId));
  }

  public ProjectTopicName createTopic(final ProjectTopicName topicName) throws IOException {
    try (TopicAdminClient topicAdmin = TopicAdminClient.create()) {
      topicAdmin.createTopic(topicName);

    } catch (Exception exception) {
      throw new IOException("Unable to close TopicAdminClient", exception);
    }

    return topicName;
  }

  public ProjectTopicName getTopic(final String topicId) {
    return ProjectTopicName.of(projectId, topicId);
  }

  public Subscription createSubscription(
      final String subscriptionId, final ProjectTopicName topicName) throws IOException {
    return createSubscription(ProjectSubscriptionName.of(projectId, subscriptionId), topicName);
  }

  public Subscription createSubscription(
      final ProjectSubscriptionName subscription, final ProjectTopicName topicName)
      throws IOException {
    try {
      try (final SubscriptionAdminClient subAdmin = SubscriptionAdminClient.create()) {
        // eg. projectId = "my-test-project", topicId = "my-test-topic"
        // eg. subscriptionId = "my-test-subscription"
        // create a pull subscription with default acknowledgement deadline
        return subAdmin.createSubscription(
            subscription, topicName, PushConfig.getDefaultInstance(), 0);
      }
    } catch (Exception exception) {
      throw new IOException("Unable to close SubscriptionAdminClient", exception);
    }
  }

  public void publishMessage(final TopicName topicName, final String body) throws IOException {
    final List<ApiFuture<String>> messageIdFutures = new ArrayList<>();
    Publisher publisher = null;

    try {
      // Create a publisher instance with default settings bound to the topic
      publisher = Publisher.newBuilder(topicName).build();

      // schedule publishing one message at a time : messages get automatically batched
      final ByteString data = ByteString.copyFromUtf8(body);
      final PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      // Once published, returns a server-assigned message id (unique within the topic)
      final ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      messageIdFutures.add(messageIdFuture);
    } finally {
      // wait on any pending publish requests.
      try {
        final List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();
        LOG.debug("Published with message ID: {}", messageIds);
      } catch (InterruptedException | ExecutionException exception) {
        throw new IOException("Unable to retrieve message IDs", exception);
      }

      if (publisher != null) {
        // When finished with the publisher, shutdown to free up resources.
        try {
          publisher.shutdown();
        } catch (Exception exception) {
          throw new IOException("Unable to shutdown publisher", exception);
        }
      }
    }
  }

  public void deleteTopic(final ProjectTopicName topic) throws IOException {
    try (final TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(topic);
    } catch (Exception exception) {
      throw new IOException("Unable to delete topic: " + topic.getTopic(), exception);
    }
  }

  public void deleteSubscription(final ProjectSubscriptionName subscription) throws IOException {
    if (subscription != null) {
      final SubscriptionAdminClient client = SubscriptionAdminClient.create();
      client.deleteSubscription(subscription);
    }
  }
}
