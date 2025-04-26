package dev.walgo.walib;

/**
 * TriOptional is a container for a value that may be null or empty.
 *
 * @param <T> value type
 */
public final class TriOptional<T> {
    private final boolean empty;
    private final T value;

    private TriOptional(boolean empty, T value) {
        this.empty = empty;
        this.value = value;
    }

    /**
     * Create empty TriOptional.
     *
     * @param <T> value type
     * @return empty TriOptional
     */
    public static <T> TriOptional<T> empty() {
        return new TriOptional<>(true, null);
    }

    /**
     * Create TriOptional from value.
     *
     * @param value value (null is possible)
     * @param <T>   value type
     * @return TriOptional with value
     */
    public static <T> TriOptional<T> of(T value) {
        return new TriOptional<>(false, value);
    }

    /**
     * Check if TriOptional is empty.
     *
     * @return true if TriOptional is empty
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Check if TriOptional contains a not null value but not empty.
     *
     * @return true if TriOptional is not empty and contains a null value
     */
    public boolean isNull() {
        return !empty && (value == null);
    }

    /**
     * Check if TriOptional is not empty.
     *
     * @return true if TriOptional contains a value (null or not null)
     */
    public boolean hasValue() {
        return !empty;
    }

    /**
     * If a value is present in this {@code TriOptional}, returns the value,
     * otherwise throws {@code IllegalStateException}.
     *
     * @return the value, if present
     * @throws IllegalStateException if no value is present
     */
    public T get() {
        if (!hasValue()) {
            throw new IllegalStateException("No value present");
        }
        return value;
    }

    @Override
    public String toString() {
        return empty ? "TriOptional[empty]" : "TriOptional[" + value + "]";
    }
}
