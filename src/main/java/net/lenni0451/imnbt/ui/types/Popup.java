package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ImNbtDrawer;

public abstract class Popup<P extends Popup<P>> {

    private final String title;
    private final PopupCallback<P> callback;
    private boolean opened = false;

    public Popup(final String title, final PopupCallback<P> callback) {
        this.title = title;
        this.callback = callback;
    }

    public String getTitle() {
        return this.title;
    }

    public PopupCallback<P> getCallback() {
        return this.callback;
    }

    public void open() {
        if (this.opened) return;
        this.opened = true;
        ImGui.openPopup(this.title);
    }

    public void close() {
        if (!this.opened) return;
        this.opened = false;
        ImGui.closeCurrentPopup();
    }

    public void render(final ImNbtDrawer drawer) {
        if (ImGui.beginPopupModal(this.title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings)) {
            this.renderContent(drawer);

            ImGui.endPopup();
        }
    }

    protected abstract void renderContent(final ImNbtDrawer drawer);


    @FunctionalInterface
    public interface PopupCallback<P> {
        static <T> PopupCallback<T> close(final ImNbtDrawer drawer) {
            return (p, success) -> drawer.closePopup();
        }

        void onClose(final P popup, final boolean success);
    }

}
