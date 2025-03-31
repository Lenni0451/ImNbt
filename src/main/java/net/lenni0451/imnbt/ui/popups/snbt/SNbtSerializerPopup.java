package net.lenni0451.imnbt.ui.popups.snbt;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.mcstructs.nbt.NbtTag;

import static net.lenni0451.imnbt.utils.nbt.SNbtUtils.SERIALIZERS;
import static net.lenni0451.imnbt.utils.nbt.SNbtUtils.SERIALIZER_NAMES;

/**
 * A popup to serialize a tag to SNbt.
 */
public class SNbtSerializerPopup extends Popup<SNbtSerializerPopup> {

    private final ImString output = new ImString(32767);
    private final ImInt selectedVersion = new ImInt(SERIALIZER_NAMES.length - 1);
    private final NbtTag tag;

    public SNbtSerializerPopup(final NbtTag tag, final PopupCallback<SNbtSerializerPopup> callback) {
        super("SNbt Serializer", callback);
        this.output.inputData.isResizable = true;

        this.tag = tag;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.combo("Version", this.selectedVersion, SERIALIZER_NAMES);
        ImGui.inputText("Output", this.output);

        if (ImGui.button("Serialize")) {
            try {
                this.output.set(SERIALIZERS.get("V" + SERIALIZER_NAMES[this.selectedVersion.get()].replace('.', '_')).serialize(this.tag));
            } catch (Throwable t) {
                drawer.showNotification(NotificationLevel.ERROR, "Error", t.getMessage());
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Copy")) {
            ImGui.setClipboardText(this.output.get());
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            this.getCallback().onClose(this, false);
            this.close();
        }
    }

}
