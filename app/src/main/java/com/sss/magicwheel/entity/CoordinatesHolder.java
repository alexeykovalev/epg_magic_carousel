package com.sss.magicwheel.entity;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class CoordinatesHolder {

    private final double x;
    private final double y;

    private CoordinatesHolder(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static CoordinatesHolder ofPolar(double radius, double angle) {
        return CoordinatesHolder.ofRect(radius * Math.cos(angle), radius * Math.sin(angle));
    }

    public static CoordinatesHolder ofRect(double x, double y) {
        return new CoordinatesHolder(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // todo: consider the enum Unit {Double, Float, Integer} strategy value for return types.
    public float getXAsFloat() {
        return (float) x;
    }

    public float getYAsFloat() {
        return (float) y;
    }

    public double getAngleInRad() {
        return Math.atan2(y, x); //Math.atan(y / x);
    }

    public double getRadius() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinatesHolder that = (CoordinatesHolder) o;
        return Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("CoordinatesHolder (x, y) [%s; %s]", x, y);
    }
}
