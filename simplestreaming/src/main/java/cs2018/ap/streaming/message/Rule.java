package cs2018.ap.streaming.message;

import java.util.List;

public class Rule {
  private int id;

  private List<Keyword> keywords;

  private NamedEntity topics;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Keyword> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<Keyword> keywords) {
    this.keywords = keywords;
  }

  public NamedEntity getTopics() {
    return topics;
  }

  public void setTopics(NamedEntity topics) {
    this.topics = topics;
  }
}
