package net.lenni0451.imnbt.ui.popups.snbt;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.mcstructs.nbt.INbtTag;

import static net.lenni0451.imnbt.utils.nbt.SNbtUtils.SERIALIZERS;
import static net.lenni0451.imnbt.utils.nbt.SNbtUtils.SERIALIZER_NAMES;

/**
 * A popup to parse SNbt to a tag.
 */
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
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.combo("Version", this.selectedVersion, SERIALIZER_NAMES);
        ImGui.inputText("Input", this.input);

        if (ImGui.button("Parse")) {
            try {
                this.parsedTag = SERIALIZERS.get("V" + SERIALIZER_NAMES[this.selectedVersion.get()].replace('.', '_')).deserialize(this.input.get());
                this.getCallback().onClose(this, true);
                this.close();
            } catch (Throwable t) {
                drawer.showNotification(NotificationLevel.ERROR, "Error", t.getMessage());
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            this.getCallback().onClose(this, false);
            this.close();
        }
    }

}
