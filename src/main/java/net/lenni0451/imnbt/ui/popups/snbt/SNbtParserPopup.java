package net.lenni0451.imnbt.ui.popups.snbt;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.mcstructs.nbt.INbtTag;

import static net.lenni0451.imnbt.utils.nbt.SNbtUtils.SERIALIZERS;
import static net.lenni0451.imnbt.utils.nbt.SNbtUtils.SERIALIZER_NAMES;

public class SNbtParserPopup extends Popup<SNbtParserPopup> {

    private final ImString input = new ImString(32767);
    private final ImInt selectedVersion = new ImInt(SERIALIZER_NAMES.length - 1);
    private INbtTag parsedTag;

    public SNbtParserPopup(final PopupCallback<SNbtParserPopup> callback) {
        super("SNbt Parser", callback);
        this.input.inputData.isResizable = true;
    }

    public INbtTag getParsedTag() {
        return this.parsedTag;
    }

    @Override
    protected void renderContent() {
        ImGui.combo("Version", this.selectedVersion, SERIALIZER_NAMES);
        ImGui.inputText("Input", this.input);

        if (ImGui.button("Parse")) {
            this.close();
            try {
                this.parsedTag = SERIALIZERS.get("V" + SERIALIZER_NAMES[this.selectedVersion.get()].replace('.', '_')).deserialize(this.input.get());
                this.getCallback().onClose(this, true);
            } catch (Throwable t) {
                Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", t.getMessage(), (p, success) -> Main.getInstance().getImGuiImpl().openPopup(this)));
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            this.getCallback().onClose(this, false);
            this.close();
        }
    }

}
