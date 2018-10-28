package cs2018.ap.streaming.message;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD")
@SuppressWarnings(value = {"PMD.ShortVariable", "PMD.AbstractNaming"})
public abstract class RawMessage implements Serializable {

  private static final long serialVersionUID = -4375477310170453396L;

  @JsonProperty(value = "_id")
  private String id;

  public abstract EnrichedMessage toEnrichedMsg();

  public abstract RawMessage cloneMsg();

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return String.format("RawMessage{_id='%s'}", id);
  }

  public abstract Date parseDate(final String dateStr);

  @Override
  public boolean equals(final Object raw) {
    if (this == raw) {
      return true;
    }
    if (!(raw instanceof RawMessage)) {
      return false;
    }
    final RawMessage that = (RawMessage) raw;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
