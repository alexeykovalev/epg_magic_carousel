package com.sss.magicwheel.entity;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
public class LinearClipData {

    private final CoordinatesHolder first;
    private final CoordinatesHolder second;
    private final CoordinatesHolder third;
    private final CoordinatesHolder fourth;

    public LinearClipData(CoordinatesHolder first, CoordinatesHolder second, CoordinatesHolder third, CoordinatesHolder fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public CoordinatesHolder getFirst() {
        return first;
    }

    public CoordinatesHolder getSecond() {
        return second;
    }

    public CoordinatesHolder getThird() {
        return third;
    }

    public CoordinatesHolder getFourth() {
        return fourth;
    }

    @Override
    public String toString() {
        return "LinearClipData{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", fourth=" + fourth +
                '}';
    }
}
