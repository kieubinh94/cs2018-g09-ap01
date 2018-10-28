package cs2018.ap.streaming.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({
  "PMD.UselessStringValueOf",
  "PMD.LooseCoupling",
  "PMD.ReplaceVectorWithList",
  "unchecked"
})
public final class PropertiesUtils {
  private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtils.class);
  private static final String UNKNOWN_VERSION = "UNKNOWN";

  private PropertiesUtils() {}

  public static Map<String, Object> loadResourceConfig(final List<String> configPaths)
      throws IOException {

    final Vector<InputStream> streams = getResourceFiles(configPaths);

    final SequenceInputStream configFiles = new SequenceInputStream(streams.elements());
    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      return mapper.readValue(configFiles, Map.class);
    } catch (IOException e) {
      LOG.error("Could not parse config files, please check!", e);
      throw e;
    }
  }

  public static String toBeamParamStyle(final String key, final Object value) {
    return String.format("%s%s%s%s", "--", key, "=", String.valueOf(value));
  }

  private static Vector<InputStream> getResourceFiles(final List<String> paths) {
    return paths
        .stream()
        .map(
            resource ->
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resource))
        .collect(Collectors.toCollection(Vector::new));
  }

  public static String getMessagePipelineVersion() {
    final Class clazz = PropertiesUtils.class;
    final URL classURL = clazz.getResource(clazz.getSimpleName() + ".class");
    final String protocol = classURL.getProtocol();
    final String classPath = classURL.toString();

    if (Strings.isNotEmpty(protocol) && classPath.startsWith(protocol)) {
      final String manifestFilePath =
          classPath.substring(0, classPath.lastIndexOf('!') + 1) + "/META-INF/MANIFEST.MF";
      final Optional<Manifest> manifest = loadManifest(manifestFilePath);
      if (!manifest.isPresent()) {
        return UNKNOWN_VERSION;
      }
      final Attributes mainAttributes = manifest.get().getMainAttributes();
      final String version = mainAttributes.getValue("Implementation-Version");

      if (version != null) {
        LOG.info("Current version: {}", version);
        return version;
      }
    }

    return UNKNOWN_VERSION;
  }

  private static Optional<Manifest> loadManifest(final String manifestFilePath) {
    try {
      return Optional.of(new Manifest(new URL(manifestFilePath).openStream()));
    } catch (IOException ex) {
      LOG.warn("Cannot load manifest to get version");
    }
    return Optional.empty();
  }
}
