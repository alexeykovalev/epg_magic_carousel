package com.magicwheel.func;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.magicwheel.func.Preconditions.checkNotNull;


/**
 * An immutable object that may contain a non-null reference to another object. Each
 * instance of this type either contains a non-null reference, or contains nothing (in
 * which case we say that the reference is "absent"); it is never said to "contain {@code
 * null}".
 * <p>
 * <p>A non-null {@code Optional<T>} reference can be used as a replacement for a nullable
 * {@code T} reference. It allows you to represent "a {@code T} that must be present" and
 * a "a {@code T} that might be absent" as two distinct types in your program, which can
 * aid clarity.
 * <p>
 * <p>Some uses of this class include
 * <p>
 * <ul>
 * <li>As a method return type, as an alternative to returning {@code null} to indicate
 * that no value was available
 * <li>To distinguish between "unknown" (for example, not present in a map) and "known to
 * have no value" (present in the map, with value {@code Optional.absent()})
 * <li>To wrap nullable references for storage in a collection that does not support
 * {@code null} (though there are
 * <a href="http://code.google.com/p/guava-libraries/wiki/LivingWithNullHostileCollections">
 * several other approaches to this</a> that should be considered first)
 * </ul>
 * <p>
 * <p>A common alternative to using this class is to find or create a suitable
 * <a href="http://en.wikipedia.org/wiki/Null_Object_pattern">null object</a> for the
 * type in question.
 * <p>
 * <p>This class is not intended as a direct analogue of any existing "option" or "maybe"
 * construct from other programming environments, though it may bear some similarities.
 * <p>
 * <p>See the Guava User Guide article on <a
 * href="http://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained#Optional">
 * using {@code Optional}</a>.
 *
 * @param <T> the type of instance that can be contained. {@code Optional} is naturally
 *            covariant on this type, so it is safe to cast an {@code Optional<T>} to {@code
 *            Optional<S>} for any supertype {@code S} of {@code T}.
 * @author Kurt Alfred Kluever
 * @author Kevin Bourrillion
 * @since 10.0
 */
public abstract class Optional<T> implements Serializable {
    /**
     * Returns an {@code Optional} instance with no contained reference.
     */
    public static <T> Optional<T> absent() {
        return Absent.withType();
    }

    /**
     * Returns an {@code Optional} instance containing the given non-null reference.
     */
    public static <T> Optional<T> of(T reference) {
        return new Present<>(checkNotNull(reference));
    }

    /**
     * If {@code nullableReference} is non-null, returns an {@code Optional} instance containing that
     * reference; otherwise returns {@link Optional#absent}.
     */
    public static <T> Optional<T> fromNullable(T nullableReference) {
        return (nullableReference == null)
                ? Optional.<T>absent()
                : new Present<>(nullableReference);
    }

    Optional() {
    }

    /**
     * Returns {@code true} if this holder contains a (non-null) instance.
     */
    public abstract boolean isPresent();

    public final boolean isAbsent() {
        return !isPresent();
    }

    /**
     * Returns the contained instance, which must be present. If the instance might be
     * absent, use {@link #or(Object)} or {@link #orNull} instead.
     *
     * @throws IllegalStateException if the instance is absent ({@link #isPresent} returns
     *                               {@code false})
     */
    public abstract T get();

    /**
     * Returns the contained instance if it is present; {@code defaultValue} otherwise. If
     * no default value should be required because the instance is known to be present, use
     * {@link #get()} instead. For a default value of {@code null}, use {@link #orNull}.
     * <p>
     * <p>Note about generics: The signature {@code public T or(T defaultValue)} is overly
     * restrictive. However, the ideal signature, {@code public <S super T> S or(S)}, is not legal
     * Java. As a result, some sensible operations involving subtypes are compile errors:
     * <pre>   {@code
     * <p>
     *   Optional<Integer> optionalInt = getSomeOptionalInt();
     *   Number value = optionalInt.or(0.5); // error
     * <p>
     *   FluentIterable<? extends Number> numbers = getSomeNumbers();
     *   Optional<? extends Number> first = numbers.first();
     *   Number value = first.or(0.5); // error}</pre>
     * <p>
     * <p>As a workaround, it is always safe to cast an {@code Optional<? extends T>} to {@code
     * Optional<T>}. Casting either of the above example {@code Optional} instances to {@code
     * Optional<Number>} (where {@code Number} is the desired output type) solves the problem:
     * <pre>   {@code
     * <p>
     *   Optional<Number> optionalInt = (Optional) getSomeOptionalInt();
     *   Number value = optionalInt.or(0.5); // fine
     * <p>
     *   FluentIterable<? extends Number> numbers = getSomeNumbers();
     *   Optional<Number> first = (Optional) numbers.first();
     *   Number value = first.or(0.5); // fine}</pre>
     */
    public abstract T or(T defaultValue);

    /**
     * Returns this {@code Optional} if it has a value present; {@code secondChoice}
     * otherwise.
     */
    public abstract Optional<T> or(Optional<? extends T> secondChoice);

    /**
     * Returns the contained instance if it is present; {@code supplier.get()} otherwise. If the
     * supplier returns {@code null}, a {@link NullPointerException} is thrown.
     *
     * @throws NullPointerException if the supplier returns {@code null}
     */
    public abstract T or(Supplier<? extends T> supplier);

    /**
     * Returns the contained instance if it is present; {@code null} otherwise. If the
     * instance is known to be present, use {@link #get()} instead.
     */
    public abstract T orNull();

    public final <E extends RuntimeException> T getOrThrow(E exceptionToThrow) {
        if (!isPresent()) {
            throw exceptionToThrow;
        }
        return get();
    }

    public final <E extends Exception> T getOrThrow(E exceptionToThrow) throws E {
        if (!isPresent()) {
            throw exceptionToThrow;
        }
        return get();
    }

    /**
     * Returns an immutable singleton {@link Set} whose only element is the contained instance
     * if it is present; an empty immutable {@link Set} otherwise.
     *
     * @since 11.0
     */
    public abstract Set<T> asSet();

    /**
     * If the instance is present, it is transformed with the given {@link Function1}; otherwise,
     * {@link Optional#absent} is returned. If the function returns {@code null}, a
     * {@link NullPointerException} is thrown.
     *
     * @throws NullPointerException if the function returns {@code null}
     * @since 12.0
     */
    public abstract <V> Optional<V> map(Function1<? super T, V> function);

    public abstract <V> Optional<V> flatMap(Function1<? super T, Optional<V>> function);

    public abstract Optional<T> doOnPresent(Consumer<? super T> consumer);

    public abstract Optional<T> doOnAbsent(Action actionToExecute);

    public abstract Optional<T> onAbsentReturn(Function0<Optional<T>> function);

    public abstract Optional<T> filter(Function1<? super T, Boolean> predicate);

    public abstract <E> Optional<E> toType(Class<E> type);

    /**
     * Returns {@code true} if {@code object} is an {@code Optional} instance, and either
     * the contained references are {@linkplain Object#equals equal} to each other or both
     * are absent. Note that {@code Optional} instances of differing parameterized types can
     * be equal.
     */
    @Override
    public abstract boolean equals(Object object);

    /**
     * Returns a hash code for this instance.
     */
    @Override
    public abstract int hashCode();

    /**
     * Returns a string representation for this instance. The form of this string
     * representation is unspecified.
     */
    @Override
    public abstract String toString();

    /**
     * Returns the value of each present instance from the supplied {@code optionals}, in order,
     * skipping over occurrences of {@link Optional#absent}. Iterators are unmodifiable and are
     * evaluated lazily.
     *
     * @since 11.0 (generics widened in 13.0)
     */
    public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> optionals) {
        checkNotNull(optionals);
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {
                    private final Iterator<? extends Optional<? extends T>> iterator =
                            checkNotNull(optionals.iterator());

                    @Override
                    protected T computeNext() {
                        while (iterator.hasNext()) {
                            Optional<? extends T> optional = iterator.next();
                            if (optional.isPresent()) {
                                return optional.get();
                            }
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    public static <T> List<T> presentInstancesAsList(final Iterable<? extends Optional<? extends T>> optionals) {
        final List<T> res = new ArrayList<>();
        for (T instance : presentInstances(optionals)) {
            res.add(instance);
        }
        return res;
    }

    private static final long serialVersionUID = 0;
}