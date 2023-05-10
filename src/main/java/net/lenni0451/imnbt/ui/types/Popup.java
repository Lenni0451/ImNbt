package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ImNbtDrawer;

/**
 * A popup that can be shown.<br>
 * There can only be one popup shown at a time.
 *
 * @param <P> The type of the popup
 */
public abstract class Popup<P extends Popup<P>> {

    private final String title;
    private final PopupCallback<P> callback;
    private boolean opened = false;

    public Popup(final String title, final PopupCallback<P> callback) {
        this.title = title;
        this.callback = callback;
    }

    /**
     * Get the title of the popup.
     *
     * @return The title of the popup
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Get the callback of the popup.
     *
     * @return The callback of the popup
     */
    public PopupCallback<P> getCallback() {
        return this.callback;
    }

    /**
     * Open this popup and close the current one.
     */
    public void open() {
        if (this.opened) return;
        this.opened = true;
        ImGui.openPopup(this.title);
    }

    /**
     * Close this popup.
     */
    public void close() {
        if (!this.opened) return;
        this.opened = false;
        ImGui.closeCurrentPopup();
    }

    /**
     * Render the popup content.
     *
     * @param drawer The drawer instance
     */
    public void render(final ImNbtDrawer drawer) {
        if (ImGui.beginPopupModal(this.title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings)) {
            this.renderContent(drawer);

            ImGui.endPopup();
        }
    }

    /**
     * Render the popup content.
     *
     * @param drawer The drawer instance
     */
    protected abstract void renderContent(final ImNbtDrawer drawer);


    /**
     * Listener for when a popup is closed.
     *
     * @param <P> The type of the popup
     */
    @FunctionalInterface
    public interface PopupCallback<P> {
        /**
         * Get a callback that closes the popup ignoring the result.
         *
         * @param drawer The drawer instance
         * @param <T>    The type of the popup
         * @return The callback
         */
        static <T> PopupCallback<T> close(final ImNbtDrawer drawer) {
            return (p, success) -> drawer.closePopup();
        }

        /**
         * Handle the popup being closed.
         *
         * @param popup   The popup that was closed
         * @param success If the popup was closed with a success
         */
        void onClose(final P popup, final boolean success);
    }

}
