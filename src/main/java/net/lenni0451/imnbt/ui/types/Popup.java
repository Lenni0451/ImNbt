package net.lenni0451.imnbt.ui.types;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

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

    public void render() {
        if (ImGui.beginPopupModal(this.title, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings)) {
            this.renderContent();

            ImGui.endPopup();
        }
    }

    protected abstract void renderContent();


    @FunctionalInterface
    public interface PopupCallback<P> {
        void onClose(final P popup, final boolean success);
    }

}
