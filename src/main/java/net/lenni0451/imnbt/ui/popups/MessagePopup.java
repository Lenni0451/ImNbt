package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.types.Popup;

public class MessagePopup extends Popup {

    private final String message;

    public MessagePopup(final String title, final String message, final PopupCallback callback) {
        super(title, callback);

        this.message = message;
    }

    @Override
    protected void renderContent() {
        ImGui.text(this.message);
        ImGui.separator();
        if (ImGui.button("Ok")) {
            this.close();
            this.getCallback().onClose(true);
        }
    }

}
