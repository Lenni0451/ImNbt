package net.lenni0451.imnbt.ui.types;

import net.lenni0451.imnbt.Main;

import java.io.File;

public abstract class Window {

    public final void show() {
        Main.getInstance().getImGuiImpl().showWindow(this);
    }

    public final void hide() {
        Main.getInstance().getImGuiImpl().showWindow(Main.getInstance().getImGuiImpl().getMainWindow());
    }

    public abstract void render();

    public void dragAndDrop(final File file, final byte[] data) {
    }

}
