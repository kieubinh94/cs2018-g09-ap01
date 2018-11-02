package cs2018.ap.api.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
public enum JsonConverter {
  INSTANCE;

  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public <T> T parse(String jsonString, Class<T> clazz) {
    try {
      return mapper.readValue(jsonString, clazz);
    } catch (Exception e) {
      return null;
    }
  }

  public <T> List<T> parseAsList(String jsonString, Class<T> clazz) {
    try {
      return mapper.readValue(
          jsonString, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    } catch (Exception e) {
      return null;
    }
  }

  public <T> Set<T> parseAsSet(String jsonString, Class<T> clazz) {
    try {
      return mapper.readValue(
          jsonString, mapper.getTypeFactory().constructCollectionType(Set.class, clazz));
    } catch (Exception e) {
      return null;
    }
  }

  public String stringify(Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (Exception e) {
      return null;
    }
  }
}
