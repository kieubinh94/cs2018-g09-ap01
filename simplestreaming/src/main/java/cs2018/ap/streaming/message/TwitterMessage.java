package cs2018.ap.streaming.message;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD")
@SuppressWarnings(
  value = {
    "PMD.BeanMembersShouldSerialize",
    "PMD.ShortVariable",
    "PMD.LongVariable",
    "PMD.VariableNamingConventions",
    "PMD.ClassNamingConventions",
    "PMD.MethodNamingConventions",
    "PMD.ShortClassName",
    "PMD.GodClass",
    "PMD.ExcessivePublicCount",
    "PMD.TooManyFields",
    "PMD.ArrayIsStoredDirectly",
    "PMD.AvoidDuplicateLiterals",
    "PMD.AvoidFieldNameMatchingTypeName",
    "PMD.MethodReturnsInternalArray",
    "PMD.TooManyMethods"
  }
)
public class TwitterMessage extends RawMessage {

  @JsonProperty("_source")
  private String source;

  private String text;

  private String lang;

  @JsonProperty(value = "created_at")
  private String createdAtStr;

  @JsonProperty("id_str")
  private String idStr;

  @JsonProperty("user")
  private User user;

  @Override
  public RawMessage cloneMsg() {
    final TwitterMessage cloneMsg = new TwitterMessage();
    cloneMsg.setId(this.getId());
    cloneMsg.source = this.source;
    cloneMsg.text = this.text;
    cloneMsg.lang = this.lang;
    cloneMsg.createdAtStr = this.createdAtStr;
    cloneMsg.idStr = this.idStr;
    cloneMsg.user = this.user;
    return cloneMsg;
  }

  @Override
  public EnrichedMessage toEnrichedMsg() {
    final EnrichedMessage enrichedMessage = new EnrichedMessage();
    enrichedMessage.setId(getId());
    enrichedMessage.setContent(getText());
    enrichedMessage.setLang(getLang());
    enrichedMessage.setCreatedAt(
        TwitterDateDeserializer.INSTANCE.parse(getCreatedAtStr())
    );

    final Publisher publisher = new Publisher();
    publisher.setPartnerId(getUser().getIdStr());
    publisher.setChannel("tw");
    enrichedMessage.setPublisher(publisher);

    return enrichedMessage;
  }

  @Override
  public Date parseDate(final String dateStr) {
    return TwitterDateDeserializer.INSTANCE.parse(dateStr);
  }

  public String getSource() {
    return source;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(final String lang) {
    this.lang = lang;
  }

  public String getCreatedAtStr() {
    return createdAtStr;
  }

  public void setCreatedAtStr(final String createdAtStr) {
    this.createdAtStr = createdAtStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(final String idStr) {
    this.idStr = idStr;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public static class User implements Serializable {

    private static final long serialVersionUID = 4596855204041872586L;

    @JsonProperty("id_str")
    private String idStr;

    private String name;

    @JsonProperty("screen_name")
    private String screenName;

    @JsonProperty("profile_image_url_https")
    private String profileImageUrl;

    public User() {
      super();
    }

    @Override
    public String toString() {
      return String.format("User{idStr='%s'", idStr);
    }

    public String getIdStr() {
      return idStr;
    }

    public String getName() {
      return name;
    }

    public String getScreenName() {
      return screenName;
    }

    public String getProfileImageUrl() {
      return profileImageUrl;
    }

    @Override
    public boolean equals(final Object another) {
      if (this == another) {
        return true;
      }
      if (!(another instanceof User)) {
        return false;
      }
      final User user = (User) another;
      return Objects.equals(idStr, user.idStr)
          && Objects.equals(name, user.name)
          && Objects.equals(screenName, user.screenName)
          && Objects.equals(profileImageUrl, user.profileImageUrl);
    }

    @Override
    public int hashCode() {
      return Objects.hash(idStr, name, screenName, profileImageUrl);
    }
  }
}
