package net.lenni0451.imnbt.ui.types;

import net.lenni0451.imnbt.ImNbtDrawer;

import java.io.File;

/**
 * A window that can be shown.<br>
 * There can only be one window shown at a time.
 */
public abstract class Window {

    protected final ImNbtDrawer drawer;

    public Window(final ImNbtDrawer drawer) {
        this.drawer = drawer;
    }

    /**
     * Show this window and hide the current one.
     */
    public final void show() {
        this.drawer.showWindow(this);
    }

    /**
     * Hide this window and show the main window.
     */
    public final void hide() {
        this.drawer.showWindow(this.drawer.getMainWindow());
    }

    /**
     * Render the window content.
     */
    public abstract void render();

    /**
     * Handle a file being dropped on the window.
     *
     * @param file The file that was dropped
     * @param data The raw data of the file
     */
    public void dragAndDrop(final File file, final byte[] data) {
    }

}
