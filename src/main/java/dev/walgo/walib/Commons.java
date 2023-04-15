package dev.walgo.walib;

/**
 * Common utilities.
 *
 * @author Walery Wysotsky {@literal <dev@wysotsky.info>}
 */
public class Commons {

  private Commons() {
    // do nothing
  }

  /**
   * Return first non-null item from list.
   *
   * <p>
   * Propose to use default as last value, e.g.: Commons.coalesce(firstValue, secondValue,
   * thirdValue, defaultValue)
   *
   * @param <T>   item type
   * @param items list of items
   * @return first non-null, or null, when all are nulls
   */
  @SuppressWarnings("unchecked")
  public static <T> T coalesce(T... items) {
    for (T item : items) {
      if (item != null) {
        return item;
      }
    }
    return null;
  }
}
