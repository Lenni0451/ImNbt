package net.lenni0451.imnbt.ui.popups.snbt;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.mcstructs.nbt.INbtTag;

import static net.lenni0451.imnbt.utils.SNbtUtils.SERIALIZERS;
import static net.lenni0451.imnbt.utils.SNbtUtils.SERIALIZER_NAMES;

public class SNbtSerializerPopup extends Popup<SNbtSerializerPopup> {

    private final ImString output = new ImString(32767);
    private final ImInt selectedVersion = new ImInt(SERIALIZER_NAMES.length - 1);
    private final INbtTag tag;

    public SNbtSerializerPopup(final INbtTag tag, final PopupCallback<SNbtSerializerPopup> callback) {
        super("SNbt Serializer", callback);

        this.tag = tag;
    }

    @Override
    protected void renderContent() {
        ImGui.combo("Version", this.selectedVersion, SERIALIZER_NAMES);
        ImGui.inputText("Output", this.output);

        if (ImGui.button("Serialize")) {
            try {
                this.output.set(SERIALIZERS.get("V" + SERIALIZER_NAMES[this.selectedVersion.get()].replace('.', '_')).serialize(this.tag));
            } catch (Throwable t) {
                ImGuiImpl.getInstance().getMainWindow().openPopup(new MessagePopup("Error", t.getMessage(), (p, success) -> ImGuiImpl.getInstance().getMainWindow().openPopup(this)));
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
