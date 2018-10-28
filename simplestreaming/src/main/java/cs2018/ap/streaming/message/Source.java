package cs2018.ap.streaming.message;

public enum Source {
  TW_GNIP1("gnip"),
  TW_GNIP2("gnip2"),
  TW_REST("rest"),
  TW_STREAM("stream"),

  MOREOVER("moreover"),
  RSS("rss");

  private final String name;

  Source(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static boolean isPubRule(final String source) {
    return TW_GNIP2.getName().equals(source);
  }
}
