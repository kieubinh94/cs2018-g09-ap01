package cs2018.ap.streaming.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings({
  "PMD.ShortMethodName",
  "PMD.AbstractNaming",
  "PMD.ShortVariable",
  "PMD.LongVariable",
  "PMD.AvoidDuplicateLiterals",
  "PMD.TooManyMethods"
})
public class NamedEntity implements Serializable {

  private static final long serialVersionUID = -2609917381174027501L;

  public static final int COMPANY = 1;

  private int id;
  private int type;

  private String name;

  @JsonProperty("country_code")
  private String countryCode;

  private String description;

  public NamedEntity() {
    super();
  }

  public NamedEntity(final int id, final String name, final String countryCode) {
    this.id = id;
    this.name = name;
    this.countryCode = countryCode;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(final String countryCode) {
    this.countryCode = countryCode;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, Object> toMap() {
    final Map<String, Object> map = new HashMap<>();
    map.put("id", this.id);
    map.put("name", this.name);
    map.put("country_code", this.countryCode);
    return map;
  }
}
