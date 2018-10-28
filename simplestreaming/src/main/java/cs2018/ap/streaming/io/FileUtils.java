package cs2018.ap.streaming.io;

import static com.google.common.base.Preconditions.checkArgument;

import cs2018.ap.streaming.utils.JacksonConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;

public final class FileUtils {

  private FileUtils() {}

  public static void jsonToLine(final String input, final String output) throws IOException {
    List<Map<String, Object>> objs =
        JacksonConverter.INSTANCE.parseJsonToObject(FileUtils.read(Paths.get(input)), List.class);

    for (final Map<String, Object> obj : objs) {
      Files.write(
          Paths.get(output),
          (JacksonConverter.INSTANCE.writeObjectToJson(obj) + "\n")
              .getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.APPEND);
    }
  }

  public static String read(final InputStream stream) throws IOException {
    final StringWriter writer = new StringWriter();
    IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
    return writer.toString();
  }

  public static List<String> readLines(final Path filePath) throws IOException {
    return Files.readAllLines(filePath);
  }

  public static String read(final Path filePath) throws IOException {
    return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
  }

  public static String read(final Path filePath, final String charsetName) throws IOException {
    return new String(Files.readAllBytes(filePath), charsetName);
  }

  public static List<String> readFilesInDir(final Path dir) throws IOException {
    checkArgument(dir.toFile().isDirectory());
    final List<Path> outFiles =
        Files.list(dir).filter(item -> item.toFile().isFile()).collect(Collectors.toList());
    final List<String> fileContents = new ArrayList<>();
    for (final Path outFile : outFiles) {
      fileContents.addAll(Files.readAllLines(outFile));
    }
    return fileContents;
  }

  public static long numberOfFiles(final Path dir) throws IOException {
    return Files.list(dir).count();
  }

  public static void delete(final Path path) throws IOException {
    if (path.toFile().exists()) {
      Files.walk(path)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .peek(System.out::println)
          .forEach(File::deleteOnExit);
      path.toAbsolutePath().toFile().deleteOnExit();
    }
  }
}
