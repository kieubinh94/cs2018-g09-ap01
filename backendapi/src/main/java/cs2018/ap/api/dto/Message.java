package cs2018.ap.api.dto;

import cs2018.ap.api.entities.NamedEntity;
import cs2018.ap.api.entities.Poster;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Message {
  private String msgId;
  private String content;
  private Date createdAt;

  private Poster poster;

  private List<NamedEntity> namedEntities;
}
