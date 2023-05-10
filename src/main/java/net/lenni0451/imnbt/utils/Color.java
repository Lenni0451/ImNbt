package net.lenni0451.imnbt.utils;

/**
 * A simple color class used for styling.
 */
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

    /**
     * Get the red value of the color.
     *
     * @return The red value of the color
     */
    public int getR() {
        return this.r;
    }

    /**
     * Get the green value of the color.
     *
     * @return The green value of the color
     */
    public int getG() {
        return this.g;
    }

    /**
     * Get the blue value of the color.
     *
     * @return The blue value of the color
     */
    public int getB() {
        return this.b;
    }

    /**
     * Get the alpha value of the color.
     *
     * @return The alpha value of the color
     */
    public int getA() {
        return this.a;
    }

    /**
     * Get the color as an ABGR int used by ImGui for styles.
     *
     * @return The color as an ABGR int
     */
    public int getABGR() {
        return (this.a << 24) | (this.b << 16) | (this.g << 8) | this.r;
    }

}
