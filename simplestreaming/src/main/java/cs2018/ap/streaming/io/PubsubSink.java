package cs2018.ap.streaming.io;

import com.google.pubsub.v1.ProjectTopicName;

@SuppressWarnings({"PMD.ShortMethodName", "PMD.BeanMembersShouldSerialize", "PMD.LongVariable"})
public final class PubsubSink {

  private final String project;
  private final String topic;

  public static PubsubSink of(final String project, final String topic) {
    return new PubsubSink(project, topic);
  }

  private PubsubSink(final String project, final String topic) {
    this.project = project;
    this.topic = topic;
  }

  public String getPubsubAddress() {
    return ProjectTopicName.of(project, topic).toString();
  }

  public String getPubsubId() {
    return String.format("%s: %s", "PUBSUB-SINK", topic);
  }
}
