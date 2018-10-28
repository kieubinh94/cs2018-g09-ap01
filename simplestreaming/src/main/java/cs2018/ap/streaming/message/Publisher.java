package cs2018.ap.streaming.message;

import java.io.Serializable;
import java.util.*;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings({
  "PMD.LongVariable",
  "PMD.BeanMembersShouldSerialize",
  "PMD.AvoidFieldNameMatchingMethodName",
  "PMD.ExcessiveParameterList",
  "PMD.TooManyFields",
  "PMD.AvoidDuplicateLiterals",
  "PMD.GodClass"
})
public class Publisher implements Serializable {

  public static final Publisher TRUE = new Publisher();

  private static final long serialVersionUID = -2609917381174027500L;

  @JsonProperty("channel")
  private String channel;

  @JsonProperty("partner_id")
  private String partnerId;

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("screen_name")
  private String screenName;

  private int status;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  @JsonProperty("country_code")
  private String countryCode;

  @JsonProperty("created_at")
  private Date createdAt;

  private boolean publisher;

  public Publisher() {
    super();
  }

  public Publisher(
      final String channel,
      final String partnerId,
      final int status,
      final String avatarUrl,
      final String countryCode,
      final String displayName,
      final String screenName,
      final Date createdAt) {
    this.channel = channel;
    this.partnerId = partnerId;
    this.status = status;
    this.avatarUrl = avatarUrl;
    this.countryCode = countryCode;
    this.displayName = displayName;
    this.screenName = screenName;
    this.createdAt = new Date(createdAt.getTime());
  }

  public Publisher(final Publisher other) {
    this.channel = other.channel;
    this.partnerId = other.partnerId;
    this.status = other.status;
    this.avatarUrl = other.avatarUrl;
    this.countryCode = other.countryCode;
    this.displayName = other.displayName;
    this.screenName = other.screenName;
    this.createdAt = other.createdAt;
    this.publisher = other.publisher;
  }

  @Override
  public String toString() {
    return "Publisher{"
        + "channel='"
        + channel
        + '\''
        + ", partnerId='"
        + partnerId
        + '\''
        + ", status="
        + status
        + ", avatarUrl='"
        + avatarUrl
        + '\''
        + ", countryCode='"
        + countryCode
        + '\''
        + ", displayName='"
        + displayName
        + '\''
        + ", screenName='"
        + screenName
        + '\''
        + ", createdAt="
        + createdAt
        + '}';
  }

  public String getChannel() {
    return channel;
  }

  public Publisher setChannel(final String channel) {
    this.channel = channel;
    return this;
  }

  public String getPartnerId() {
    return partnerId;
  }

  public Publisher setPartnerId(final String partnerId) {
    this.partnerId = partnerId;
    return this;
  }

  public int getStatus() {
    return status;
  }

  public Publisher setStatus(final int status) {
    this.status = status;
    return this;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(final String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(final String countryCode) {
    this.countryCode = countryCode;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  public String getScreenName() {
    return screenName;
  }

  public void setScreenName(final String screenName) {
    this.screenName = screenName;
  }

  public boolean isPublisher() {
    return publisher;
  }

  public void setPublisher(boolean publisher) {
    this.publisher = publisher;
  }

  public Date getCreatedAt() {
    if (Objects.nonNull(createdAt)) {
      return new Date(createdAt.getTime());
    } else {
      return null;
    }
  }

  public void setCreatedAt(final Date createdAt) {
    if (Objects.nonNull(createdAt)) {
      this.createdAt = new Date(createdAt.getTime());
    }
  }

  @Override
  public boolean equals(final Object another) {
    if (this == another) {
      return true;
    }
    if (another == null || getClass() != another.getClass()) {
      return false;
    }
    final Publisher that = (Publisher) another;
    return Objects.equals(channel, that.channel) && Objects.equals(partnerId, that.partnerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, partnerId);
  }

  public Map<String, Object> toMap() {
    final Map<String, Object> map = new HashMap<>();
    map.put("channel", this.getChannel());
    map.put("partner_id", this.getPartnerId());
    map.put("status", this.getStatus());
    map.put("avatar_url", this.getAvatarUrl());
    map.put("country_code", this.getCountryCode());
    return map;
  }
}
