package cs2018.ap.streaming.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

public enum JacksonConverter {
  INSTANCE;

  private transient ObjectMapper mapper;

  JacksonConverter() {
    // not allowed to create new instance outsider
  }

  public <T> T parseJsonToObject(final String jsonContent, final Class<T> clazz)
      throws IOException {
    return getObjectMapper().readValue(jsonContent, clazz);
  }

  public <T> T parseJsonToObject(final InputStream strem, final Class<T> clazz) throws IOException {
    return getObjectMapper().readValue(strem, clazz);
  }

  public <T> List<T> parseJsonToList(final String jsonContent, final Class<T> clazz)
      throws IOException {
    final org.codehaus.jackson.map.ObjectMapper objMapper = getObjectMapper();
    final CollectionType collectionType =
        objMapper.getTypeFactory().constructCollectionType(List.class, clazz);
    return objMapper.readValue(jsonContent, collectionType);
  }

  public String writeObjectToJson(final Object object) {
    try {
      final org.codehaus.jackson.map.ObjectMapper mapper = getObjectMapper();
      return mapper.writeValueAsString(object);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Cannot parse object to json");
    }
  }

  private org.codehaus.jackson.map.ObjectMapper getObjectMapper() {
    if (mapper == null) {
      mapper = new org.codehaus.jackson.map.ObjectMapper();
      mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.setVisibilityChecker(
          mapper
              .getSerializationConfig()
              .getDefaultVisibilityChecker()
              .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
              .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
              .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
              .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }
    return mapper;
  }
}
