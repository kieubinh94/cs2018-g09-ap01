package cs2018.ap.streaming.message;

import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressFBWarnings(value = {"UWF_UNWRITTEN_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class EnrichedMessage implements Serializable {

  private static final long serialVersionUID = -2001070150030021354L;

  @JsonProperty("_id")
  private String id;

  private String source;
  private String channel;

  @JsonProperty("source_url")
  private String sourceUrl;

  private String content;
  private String lang;

  @JsonProperty("content_html")
  private String htmlContent;

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("updated_at")
  private Date updatedAt;

  @JsonProperty("published_at")
  private Date publishedAt;

  @JsonProperty("published_by")
  private Publisher publisher;

  @JsonProperty("contain_bbl_word")
  private boolean containBblWord;

  @JsonProperty("ne_mentions")
  private List<NamedEntity> denormalizedNamedEntities = ImmutableList.of();

  public EnrichedMessage() {
    super();

    publisher = new Publisher();
  }

  public void copyFrom(final EnrichedMessage other) {
    this.id = other.id;
    this.source = other.source;
    this.channel = other.channel;
    this.sourceUrl = other.sourceUrl;
    this.content = other.content;
    this.htmlContent = other.htmlContent;
    this.publisher = new Publisher(other.publisher);
    this.containBblWord = other.containBblWord;
    this.createdAt = other.createdAt;
    this.denormalizedNamedEntities = new ArrayList<>(other.getDenormalizedNamedEntities());
    this.updatedAt = other.updatedAt;
    this.publishedAt = other.publishedAt;
  }

  public EnrichedMessage(final EnrichedMessage other) {
    this.copyFrom(other);
  }

  public Map<String, Object> toMap() {
    final Map<String, Object> map = new HashMap<>();
    map.put("_id", this.getId());
    map.put("source", this.getSource());
    map.put("channel", this.getChannel());
    map.put("source_url", this.getSourceUrl());
    map.put("content", this.getContent());
    map.put("content_html", this.getHtmlContent());
    if (Objects.nonNull(this.getPublisher())) {
      map.put("published_by", this.getPublisher().toMap());
    }
    map.put(
        "ne_mentions",
        this.getDenormalizedNamedEntities()
            .stream()
            .map(NamedEntity::toMap)
            .collect(Collectors.toList()));

    return map;
  }

  @Override
  public boolean equals(final Object rel) {
    if (this == rel) {
      return true;
    }
    if (rel == null || getClass() != rel.getClass()) {
      return false;
    }
    final EnrichedMessage that = (EnrichedMessage) rel;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  public void setHtmlContent(String htmlContent) {
    this.htmlContent = htmlContent;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Date getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Date publishedAt) {
    this.publishedAt = publishedAt;
  }

  public Publisher getPublisher() {
    return publisher;
  }

  public void setPublisher(Publisher publisher) {
    this.publisher = publisher;
  }

  public boolean isContainBblWord() {
    return containBblWord;
  }

  public void setContainBblWord(boolean containBblWord) {
    this.containBblWord = containBblWord;
  }

  public List<NamedEntity> getDenormalizedNamedEntities() {
    return denormalizedNamedEntities;
  }

  public void setDenormalizedNamedEntities(List<NamedEntity> denormalizedNamedEntities) {
    this.denormalizedNamedEntities = denormalizedNamedEntities;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }
}
