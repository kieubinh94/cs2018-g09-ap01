package cs2018.ap.streaming.io;

import java.io.Serializable;
import java.util.Objects;

/** Document to be inserted to Elasticsearch */
@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "PMD.ShortVariable"})
public class EsDoc implements Serializable {

  private static final long serialVersionUID = 7877777428909929064L;

  private final String id;
  private final String source;

  public EsDoc(final String id, final String source) {
    this.id = id;
    this.source = source;
  }

  public String getId() {
    return id;
  }

  public String getSource() {
    return source;
  }

  public String toBulkIndex() {
    return String.format("{\"index\":{\"_id\":\"%s\"}} %n%s%n", getId(), getSource());
  }

  public String toBulkUpdate() {
    return String.format(
        "{\"update\":{\"_id\":\"%s\"}} %n%s%n",
        getId(), String.format("{\"doc\":%s}", getSource()));
  }

  @Override
  public boolean equals(final Object another) {
    if (this == another) {
      return true;
    }
    if (another == null || getClass() != another.getClass()) {
      return false;
    }
    final EsDoc esDoc = (EsDoc) another;
    return Objects.equals(id, esDoc.id) && Objects.equals(source, esDoc.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, source);
  }
}
