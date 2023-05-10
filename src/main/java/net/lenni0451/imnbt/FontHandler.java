package net.lenni0451.imnbt;

import imgui.ImFont;

/**
 * Only required to use ImNbt in a standalone application.<br>
 * You probably don't need this.
 */
public interface FontHandler {

    /**
     * Get all fonts that are available.
     *
     * @return All fonts
     */
    ImFont[] getFonts();

    /**
     * Get the currently selected font.
     *
     * @return The selected font
     */
    int getSelectedFont();

    /**
     * Get the font size.
     *
     * @param selectedFont The font to get the size from
     */
    void setSelectedFont(final int selectedFont);

}
