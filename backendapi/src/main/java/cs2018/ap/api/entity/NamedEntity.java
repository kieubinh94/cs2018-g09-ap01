package cs2018.ap.api.entity;

import cs2018.ap.api.validation.NotBlank;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class NamedEntity {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  @Column(name = "account_id")
  private Long accountId;

  @NotBlank(message = "NamedEntity name should not empty")
  private String name;

  private String token;
}
