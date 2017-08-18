package com.magicepg.func;

import java.util.Collections;
import java.util.Set;

import static com.magicepg.func.Preconditions.checkNotNull;


/**
 * Implementation of an {@link Optional} not containing a reference.
 */
final class Absent<T> extends Optional<T> {

    static final Absent<Object> INSTANCE = new Absent<Object>();

    @SuppressWarnings("unchecked") // implementation is "fully variant"
    static <T> Optional<T> withType() {
        return (Optional<T>) INSTANCE;
    }

    private Absent() {
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        throw new IllegalStateException("Optional.get() cannot be called on an absent value");
    }

    @Override
    public T or(T defaultValue) {
        return checkNotNull(defaultValue, "use Optional.orNull() instead of Optional.or(null)");
    }

    @SuppressWarnings("unchecked") // safe covariant cast
    @Override
    public Optional<T> or(Optional<? extends T> secondChoice) {
        return (Optional<T>) checkNotNull(secondChoice);
    }

    @Override
    public T or(Supplier<? extends T> supplier) {
        return checkNotNull(supplier.get(),
                "use Optional.orNull() instead of a Supplier that returns null");
    }

    @Override
    public T orNull() {
        return null;
    }

    @Override
    public Set<T> asSet() {
        return Collections.emptySet();
    }

    @Override
    public <V> Optional<V> map(Function1<? super T, V> function) {
        checkNotNull(function);
        return absent();
    }

    @Override
    public <V> Optional<V> flatMap(Function1<? super T, Optional<V>> function) {
        return absent();
    }

    @Override
    public Optional<T> doOnPresent(Consumer<? super T> consumer) {
        return this;
    }

    @Override
    public Optional<T> doOnAbsent(Action actionToExecute) {
        actionToExecute.run();
        return this;
    }

    @Override
    public Optional<T> onAbsentReturn(Function0<Optional<T>> function) {
        return function.call();
    }

    @Override
    public Optional<T> filter(Function1<? super T, Boolean> predicate) {
        return absent();
    }

    @Override
    public <E> Optional<E> toType(Class<E> type) {
        return absent();
    }

    @Override
    public boolean equals(Object object) {
        return object == this;
    }

    @Override
    public int hashCode() {
        return 0x598df91c;
    }

    @Override
    public String toString() {
        return "Optional.absent()";
    }

    private Object readResolve() {
        return INSTANCE;
    }

    private static final long serialVersionUID = 0;
}
