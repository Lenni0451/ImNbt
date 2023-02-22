package net.lenni0451.imnbt.utils;

public class Color {

    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public Color(final int r, final int g, final int b) {
        this(r, g, b, 255);
    }

    public Color(final int r, final int g, final int b, final int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int getR() {
        return this.r;
    }

    public int getG() {
        return this.g;
    }

    public int getB() {
        return this.b;
    }

    public int getA() {
        return this.a;
    }

    public int getABGR() {
        return (this.a << 24) | (this.b << 16) | (this.g << 8) | this.r;
    }

}
