package cs2018.ap.streaming.io;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tuple extends HashMap<String, Object> {
  public Tuple() {
    super();
  }

  public Tuple(final Map<? extends String, ?> map) {
    super(map);
  }

  public String getAsNullableString(final String key) {
    final Object value = get(key);
    if (Objects.nonNull(value)) {
      return value.toString();
    }
    return null;
  }

  public Integer getAsNullableInt(final String key) {
    final Object value = get(key);
    if (Objects.nonNull(value)) {
      checkArgument(value instanceof Integer);
      return (Integer) value;
    }
    return null;
  }

  public Integer getAsNullableInt(final String key, final int defaultValue) {
    final Object value = get(key);
    if (Objects.nonNull(value)) {
      checkArgument(value instanceof Integer);
      return (Integer) value;
    }
    return defaultValue;
  }

  public String[] getAsArrayOfStrings(final String key) {
    final List<?> valueAsList = getAsList(key);
    return valueAsList.toArray(new String[valueAsList.size()]);
  }

  public Integer[] getAsArrayOfInts(final String key) {
    final List<?> valueAsList = getAsList(key);
    final Integer[] ints = new Integer[valueAsList.size()];
    for (int i = 0; i < ints.length; i++) {
      final Object element = valueAsList.get(i);
      checkArgument(element instanceof Integer);
      ints[i] = (Integer) element;
    }
    return ints;
  }

  private List<?> getAsList(final String key) {
    final Object value = get(key);
    if (Objects.nonNull(value)) {
      return (List<?>) value;
    }
    return ImmutableList.of();
  }
}
