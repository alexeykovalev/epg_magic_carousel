package com.magicepg.func;

/**
 * Assists in fetching {@code Enum} constant based on specified value
 * which corresponds to this constant.A
 *
 * @author Alexey
 * @since 23.05.2016
 */
public interface EnumAsValue<T> {

    T getValue();
}