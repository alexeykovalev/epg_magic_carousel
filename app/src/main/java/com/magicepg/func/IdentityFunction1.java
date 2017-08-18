package com.magicepg.func;

/**
 * @author Alexey
 * @since 2/11/17
 */
public final class IdentityFunction1<I> implements Function1<I, I> {

    @Override
    public I call(I input) {
        return input;
    }
}
