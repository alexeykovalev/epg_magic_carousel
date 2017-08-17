package com.magicwheel.func;

import java.util.Collections;
import java.util.Set;

import static com.magicwheel.func.Preconditions.checkNotNull;


/**
 * Implementation of an {@link Optional} containing a reference.
 */
final class Present<T> extends Optional<T> {

    private final T reference;

    Present(T reference) {
        this.reference = reference;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T get() {
        return reference;
    }

    @Override
    public T or(T defaultValue) {
        checkNotNull(defaultValue, "use Optional.orNull() instead of Optional.or(null)");
        return reference;
    }

    @Override
    public Optional<T> or(Optional<? extends T> secondChoice) {
        checkNotNull(secondChoice);
        return this;
    }

    @Override
    public T or(Supplier<? extends T> supplier) {
        checkNotNull(supplier);
        return reference;
    }

    @Override
    public T orNull() {
        return reference;
    }

    @Override
    public Set<T> asSet() {
        return Collections.singleton(reference);
    }

    @Override
    public <V> Optional<V> map(Function1<? super T, V> function) {
        return new Present<>(checkNotNull(function.call(reference),
                "the Function passed to Optional.map() must not return null."));
    }

    @Override
    public <V> Optional<V> flatMap(Function1<? super T, Optional<V>> function) {
        return function.call(reference);
    }

    @Override
    public Optional<T> doOnPresent(Consumer<? super T> consumer) {
        consumer.accept(reference);
        return this;
    }

    @Override
    public Optional<T> doOnAbsent(Action action) {
        return this;
    }

    @Override
    public Optional<T> filter(Function1<? super T, Boolean> predicate) {
        return predicate.call(reference) ? of(reference) : Optional.<T>absent();
    }

    @Override
    public <E> Optional<E> toType(Class<E> type) {
        return type.isInstance(reference) ? of(type.cast(reference)) : Optional.<E>absent();
    }

    @Override
    public Optional<T> onAbsentReturn(Function0<Optional<T>> function) {
        return of(reference);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Present) {
            Present<?> other = (Present<?>) object;
            return reference.equals(other.reference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0x598df91c + reference.hashCode();
    }

    @Override
    public String toString() {
        return "Optional.of(" + reference + ")";
    }

    private static final long serialVersionUID = 0;
}
