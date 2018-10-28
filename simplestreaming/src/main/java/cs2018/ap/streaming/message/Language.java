package cs2018.ap.streaming.message;

@SuppressWarnings("PMD.ShortMethodName")
public enum Language {
  ENGLISH("en"),
  GERMAN("de"),
  CLD2_UNKNOWN_LANG("un");

  private String code;

  Language(final String code) {
    this.code = code;
  }

  public static Language of(final String lang) {
    if ("en".equalsIgnoreCase(lang)) {
      return ENGLISH;
    }

    if ("de".equalsIgnoreCase(lang)) {
      return GERMAN;
    }

    return CLD2_UNKNOWN_LANG;
  }

  public String getCode() {
    return code;
  }

  public static boolean isEnglish(final String lang) {
    return Language.of(lang).equals(ENGLISH);
  }

  public static boolean isGerman(final String lang) {
    return Language.of(lang).equals(GERMAN);
  }

  public static boolean isValidLang(final String langCode) {
    return isEnglish(langCode) || isGerman(langCode);
  }
}
