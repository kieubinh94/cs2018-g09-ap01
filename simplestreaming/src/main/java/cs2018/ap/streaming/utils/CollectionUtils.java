package cs2018.ap.streaming.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class CollectionUtils {
  private CollectionUtils() {}

  /**
   * Null-safe check if the specified collection is empty.
   *
   * <p>Null returns true.
   *
   * @param coll the collection to check, may be null
   * @return true if empty or null
   */
  public static boolean isEmpty(final Collection<?> coll) {
    return Objects.isNull(coll) || coll.isEmpty();
  }

  public static boolean isEmpty(final Map coll) {
    return Objects.isNull(coll) || coll.isEmpty();
  }

  /**
   * Null-safe check if the specified collection is not empty.
   *
   * <p>Null returns false.
   *
   * @param coll the collection to check, may be null
   * @return true if non-null and non-empty
   */
  public static boolean isNotEmpty(final Collection<?> coll) {
    return !isEmpty(coll);
  }

  public static boolean isNotEmpty(final Map coll) {
    return !isEmpty(coll);
  }
}
