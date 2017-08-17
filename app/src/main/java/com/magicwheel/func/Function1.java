package com.magicwheel.func;

/**
 * @author Alexey Kovalev
 * @since 24.11.2016
 */
public interface Function1<I, R> {

    R call(I input);
}
