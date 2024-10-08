package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;

/**
 * A popup that shows a message and a button to close it.
 */
public class MessagePopup extends Popup<MessagePopup> {

    private final String message;

    public MessagePopup(final String title, final String message, final PopupCallback<MessagePopup> callback) {
        super(title, callback);

        this.message = message;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.textUnformatted(this.message);
        ImGui.separator();
        if (ImGui.button("Ok")) {
            this.close();
            this.getCallback().onClose(this, true);
        }
    }

}
