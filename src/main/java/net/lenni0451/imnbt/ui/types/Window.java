package net.lenni0451.imnbt.ui.types;

import net.lenni0451.imnbt.ImNbtDrawer;

import java.io.File;

public abstract class Window {

    protected final ImNbtDrawer drawer;

    public Window(final ImNbtDrawer drawer) {
        this.drawer = drawer;
    }

    public final void show() {
        this.drawer.showWindow(this);
    }

    public final void hide() {
        this.drawer.showWindow(this.drawer.getMainWindow());
    }

    public abstract void render();

    public void dragAndDrop(final File file, final byte[] data) {
    }

}
