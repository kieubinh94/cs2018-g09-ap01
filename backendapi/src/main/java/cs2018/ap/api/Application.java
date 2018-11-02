package cs2018.ap.api;

import cs2018.ap.api.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@Import({SwaggerConfig.class})
public class Application {
  public static void main(String[] args) {
    new SpringApplication(Application.class).run(args);
  }

  @PostConstruct
  public void started() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
