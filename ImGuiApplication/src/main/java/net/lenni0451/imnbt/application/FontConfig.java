package net.lenni0451.imnbt.application;

import imgui.ImFont;
import net.lenni0451.imnbt.FontHandler;

import java.util.prefs.Preferences;

public class FontConfig implements FontHandler {

    private static final String KEY_NAME = "selectedFont";

    private Preferences preferences;
    private final ImFont[] fonts = new ImFont[5];
    private int selectedFont;

    public FontConfig() {
        try {
            this.preferences = Preferences.userNodeForPackage(this.getClass());
            this.selectedFont = Math.max(0, Math.min(this.preferences.getInt(KEY_NAME, 3), this.fonts.length - 1));
        } catch (Throwable t) {
            t.printStackTrace();
            this.preferences = null;
            this.selectedFont = fonts.length - 1;
        }
    }

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
        if (this.preferences != null) {
            try {
                this.preferences.putInt(KEY_NAME, selectedFont);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
