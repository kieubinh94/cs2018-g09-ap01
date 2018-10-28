package cs2018.ap.streaming.message;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;

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
    "PMD.ConfusingTernary"
  }
)
public class RssMessage extends RawMessage {
  private static final long serialVersionUID = -4248321484973419436L;

  private String title;
  private String link;

  private String summary;
  private String description;

  @Override
  public RawMessage cloneMsg() {
    final RssMessage newOne = new RssMessage();
    newOne.setId(this.getId());
    newOne.title = this.title;
    newOne.link = this.link;
    newOne.summary = this.summary;
    newOne.description = this.description;
    return newOne;
  }

  @Override
  public Date parseDate(String dateStr) {
    return null;
  }

  @Override
  public EnrichedMessage toEnrichedMsg() {
    return null;
  }
}
