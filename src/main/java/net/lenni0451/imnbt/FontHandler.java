package net.lenni0451.imnbt;

import imgui.ImFont;

public interface FontHandler {

    ImFont[] getFonts();

    int getSelectedFont();

    void setSelectedFont(final int selectedFont);

}
