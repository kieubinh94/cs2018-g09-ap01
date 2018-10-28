package cs2018.ap.streaming.io;

import com.google.common.base.Preconditions;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings({"PMD.ShortMethodName", "PMD.BeanMembersShouldSerialize"})
public final class PubsubSource {
  private final String project;
  private final String subscription;

  public static PubsubSource of(final String project, final String subscription) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(project));
    Preconditions.checkArgument(StringUtils.isNotEmpty(subscription));
    return new PubsubSource(project, subscription);
  }

  private PubsubSource(final String project, final String subscription) {
    this.project = project;
    this.subscription = subscription;
  }

  public String getPubsubAddress() {
    return ProjectSubscriptionName.of(project, subscription).toString();
  }

  public String getPubsubId() {
    return String.format("%s: %s", "PUBSUB", subscription);
  }
}
