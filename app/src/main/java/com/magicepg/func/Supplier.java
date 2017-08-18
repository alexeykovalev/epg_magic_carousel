package com.magicepg.func;

/**
 * @author Alexey Kovalev
 * @since 04.03.2017
 */
public interface Supplier<T> {
    /**
     * Retrieves an instance of the appropriate type. The returned object may or
     * may not be a new instance, depending on the implementation.
     *
     * @return an instance of the appropriate type
     */
    T get();
}
