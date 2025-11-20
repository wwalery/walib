package dev.walgo.walib;

import java.util.AbstractList;
import java.util.List;

public class ObservableList<T> extends AbstractList<T> {

    private final List<T> delegate;
    private final Runnable onChange;

    public ObservableList(List<T> delegate, Runnable onChange) {
        this.delegate = delegate;
        this.onChange = onChange;
    }

    @Override
    public T get(int i) {
        return delegate.get(i);
    }

    @Override
    public T set(int index, T element) {
        onChange.run();
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        onChange.run();
        delegate.add(index, element);
    }

    @Override
    public T remove(int index) {
        onChange.run();
        return delegate.remove(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

}
