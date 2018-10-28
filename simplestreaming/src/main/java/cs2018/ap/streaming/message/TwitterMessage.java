package cs2018.ap.streaming.message;

import cs2018.ap.streaming.io.TwitterDateDeserializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
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

  @JsonProperty("twitter_lang")
  private String twLang;

  private String lang;

  @JsonProperty(value = "created_at")
  private String createdAtStr; // bson does not support date deserializer

  @JsonProperty("id_str")
  private String idStr;

  @Override
  public RawMessage cloneMsg() {
    final TwitterMessage cloneMsg = new TwitterMessage();
    cloneMsg.setId(this.getId());
    cloneMsg.source = this.source;
    cloneMsg.text = this.text;
    cloneMsg.lang = this.lang;
    cloneMsg.twLang = this.twLang;
    cloneMsg.createdAtStr = this.createdAtStr;
    cloneMsg.idStr = this.idStr;
    return cloneMsg;
  }

  @Override
  public EnrichedMessage toEnrichedMsg() {
    return null;
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

  public String getTwLang() {
    return twLang;
  }

  public void setTwLang(final String twLang) {
    this.twLang = twLang;
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
}
