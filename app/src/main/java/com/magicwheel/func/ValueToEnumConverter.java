package com.magicwheel.func;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper utility class which assists in setting correspondence
 * between enum constant and its value (in this case enum has to
 * implement {@link EnumAsValue} interface).
 * <p/>
 * Usually {@link #fromValue(Object, Class, Enum)} has to be invoked from
 * enum's factory method like {@code fromString()} or {@code fromInt()} etc.
 *
 * @author Alexey
 * @since 23.05.2016
 */
public final class ValueToEnumConverter {

    public interface IValueComparisonStrategy<T> {
        boolean isEqual(T arg1, T arg2);
    }

    public static final class ComparisonStrategies {

        private static final IValueComparisonStrategy<?> SIMPLE_EQUALITY_COMPARISON =
                new IValueComparisonStrategy<Object>() {
                    @Override
                    public boolean isEqual(Object arg1, Object arg2) {
                        return arg1 != null && arg1.equals(arg2);
                    }
                };

        private static final IValueComparisonStrategy<?> STRING_CASE_INDEPENDENT_COMPARISON =
                new IValueComparisonStrategy<Object>() {
                    @Override
                    public boolean isEqual(Object arg1, Object arg2) {
                        return StringUtils.equalsIgnoreCase(String.valueOf(arg1), String.valueOf(arg2));
                    }
                };

        @SuppressWarnings("unchecked")
        public static <V> IValueComparisonStrategy<V> simpleEqualityStrategy() {
            return (IValueComparisonStrategy<V>) SIMPLE_EQUALITY_COMPARISON;
        }

        @SuppressWarnings("unchecked")
        public static <V> IValueComparisonStrategy<V> stringCaseIndependentEqualityStrategy() {
            return ((IValueComparisonStrategy<V>) STRING_CASE_INDEPENDENT_COMPARISON);
        }
    }


    private ValueToEnumConverter() {
        throw new AssertionError("No instances of EnumFromValueConverterHelper");
    }

    /**
     * Comparison will be done using usual {@link #equals(Object)} method.
     * Take a look on {@link } if you need custom comparison strategy.
     */
    public static <V, E extends Enum<E> & EnumAsValue<V>> E fromValue(V enumAsValue, Class<E> enumClass, E defaultValue) {
        return fromValue(enumAsValue, enumClass, defaultValue, ComparisonStrategies.<V>simpleEqualityStrategy());
    }

    public static <V, E extends Enum<E> & EnumAsValue<V>> E fromValue(V enumAsValue,
                                                                      Class<E> enumClass,
                                                                      E defaultValue,
                                                                      IValueComparisonStrategy<V> comparisonStrategy) {

        if (enumAsValue == null || enumClass == null || comparisonStrategy == null) {
            return defaultValue;
        }

        E[] enumConstants = enumClass.getEnumConstants();
        for (E enumConstant : enumConstants) {
            if (comparisonStrategy.isEqual(enumAsValue, enumConstant.getValue())) {
                return enumConstant;
            }
        }
        return defaultValue;
    }

}