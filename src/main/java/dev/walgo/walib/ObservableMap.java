package dev.walgo.walib;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ObservableMap<K, V> implements Map<K, V> {

    private final Map<K, V> delegate;
    private final Runnable onChange;

    public ObservableMap(Map<K, V> delegate, Runnable onChange) {
        this.delegate = Objects.requireNonNull(delegate);
        this.onChange = Objects.requireNonNull(onChange);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        onChange.run();
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        onChange.run();
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (!m.isEmpty()) {
            onChange.run();
        }
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        if (!delegate.isEmpty()) {
            onChange.run();
        }
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        // Mutations via keySet().remove() must be tracked
        return new ObservableKeySet(delegate.keySet());
    }

    @Override
    public Collection<V> values() {
        // Mutations via values().remove() must be tracked
        return new ObservableValues(delegate.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        // Mutations via entrySet().iterator().remove()
        // or entry.setValue() must be tracked
        return new ObservableEntrySet(delegate.entrySet());
    }

    // ========= Internal wrapper classes ============

    private class ObservableKeySet extends AbstractSet<K> {
        private final Set<K> set;

        ObservableKeySet(Set<K> set) {
            this.set = set;
        }

        @Override
        public Iterator<K> iterator() {
            Iterator<K> it = set.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public K next() {
                    return it.next();
                }

                @Override
                public void remove() {
                    onChange.run();
                    it.remove();
                }
            };
        }

        @Override
        public int size() {
            return set.size();
        }

        @Override
        public boolean remove(Object o) {
            boolean result = set.remove(o);
            if (result) {
                onChange.run();
            }
            return result;
        }

        @Override
        public void clear() {
            if (!set.isEmpty()) {
                onChange.run();
            }
            set.clear();
        }
    }

    private class ObservableValues extends AbstractCollection<V> {
        private final Collection<V> values;

        ObservableValues(Collection<V> values) {
            this.values = values;
        }

        @Override
        public Iterator<V> iterator() {
            Iterator<V> it = values.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public V next() {
                    return it.next();
                }

                @Override
                public void remove() {
                    onChange.run();
                    it.remove();
                }
            };
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public boolean remove(Object o) {
            boolean result = values.remove(o);
            if (result) {
                onChange.run();
            }
            return result;
        }

        @Override
        public void clear() {
            if (!values.isEmpty()) {
                onChange.run();
            }
            values.clear();
        }
    }

    private class ObservableEntrySet extends AbstractSet<Entry<K, V>> {
        private final Set<Entry<K, V>> set;

        ObservableEntrySet(Set<Entry<K, V>> set) {
            this.set = set;
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            Iterator<Entry<K, V>> it = set.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Entry<K, V> next() {
                    Entry<K, V> e = it.next();
                    return new ObservableEntry(e);
                }

                @Override
                public void remove() {
                    onChange.run();
                    it.remove();
                }
            };
        }

        @Override
        public int size() {
            return set.size();
        }

        @Override
        public void clear() {
            if (!set.isEmpty()) {
                onChange.run();
            }
            set.clear();
        }

        @Override
        public boolean remove(Object o) {
            boolean result = set.remove(o);
            if (result) {
                onChange.run();
            }
            return result;
        }
    }

    private class ObservableEntry implements Entry<K, V> {
        private final Entry<K, V> entry;

        ObservableEntry(Entry<K, V> entry) {
            this.entry = entry;
        }

        @Override
        public K getKey() {
            return entry.getKey();
        }

        @Override
        public V getValue() {
            return entry.getValue();
        }

        @Override
        public V setValue(V value) {
            onChange.run();
            return entry.setValue(value);
        }

        @Override
        public boolean equals(Object o) {
            return entry.equals(o);
        }

        @Override
        public int hashCode() {
            return entry.hashCode();
        }
    }
}