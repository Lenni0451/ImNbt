package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;

/**
 * A popup that shows a message with a yes and no button.
 */
public class YesNoPopup extends Popup<YesNoPopup> {

    private final String message;

    public YesNoPopup(final String title, final String message, final PopupCallback<YesNoPopup> callback) {
        super(title, callback);

        this.message = message;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.textUnformatted(this.message);
        ImGui.separator();
        if (ImGui.button("Yes")) {
            this.close();
            this.getCallback().onClose(this, true);
        }
        ImGui.sameLine();
        if (ImGui.button("No")) {
            this.close();
            this.getCallback().onClose(this, false);
        }
    }

}
