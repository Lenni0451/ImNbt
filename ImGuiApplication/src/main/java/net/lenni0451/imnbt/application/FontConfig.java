package net.lenni0451.imnbt.application;

import imgui.ImFont;
import net.lenni0451.imnbt.FontHandler;

public class FontConfig implements FontHandler {

    private final ImFont[] fonts = new ImFont[5];
    private int selectedFont = 3;

    @Override
    public ImFont[] getFonts() {
        return this.fonts;
    }

    public ImFont getFont() {
        return this.fonts[this.selectedFont];
    }

    @Override
    public int getSelectedFont() {
        return this.selectedFont;
    }

    @Override
    public void setSelectedFont(int selectedFont) {
        this.selectedFont = selectedFont;
    }

}
