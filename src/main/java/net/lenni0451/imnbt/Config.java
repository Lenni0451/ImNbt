package net.lenni0451.imnbt;

import imgui.ImFont;

public class Config {

    private final ImFont[] fonts = new ImFont[5];
    private int usedFont = 3;

    public ImFont[] getFonts() {
        return this.fonts;
    }

    public int getUsedFont() {
        return this.usedFont;
    }

    public void setUsedFont(final int usedFont) {
        this.usedFont = usedFont;
    }

}
