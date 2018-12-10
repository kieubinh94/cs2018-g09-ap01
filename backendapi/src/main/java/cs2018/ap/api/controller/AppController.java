package cs2018.ap.api.controller;

import cs2018.ap.api.dto.Message;
import cs2018.ap.api.entity.NamedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
public class AppController {

  @PostMapping("/companies")
  List<NamedEntity> searchTopics(String name) {
    return new ArrayList<>();
  }

  @PostMapping("/messages")
  List<Message> getMessages(int namedEntityId) {
    return new ArrayList<>();
  }

}
