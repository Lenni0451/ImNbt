package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;

public class EditPopup extends Popup {

    private final ImString name = new ImString(256);
    private final INbtTag tag;
    private final TagRenderer tagRenderer;

    public EditPopup(final String title, final String name, final INbtTag tag, final PopupCallback callback) {
        super(title, callback);

        this.name.set(name);
        this.tag = tag;
        this.tagRenderer = ImGuiImpl.getInstance().getMainWindow().getTagRenderer(tag.getNbtType());
    }

    public String getName() {
        return this.name.get();
    }

    public INbtTag getTag() {
        return this.tag;
    }

    @Override
    protected void renderContent() {
        ImGui.inputText("Name", this.name);
        this.tagRenderer.renderValueEditor(this.tag);

        ImGui.separator();
        if (ImGui.button("Save")) {
            this.getCallback().onClose(true);
            this.close();
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            this.getCallback().onClose(false);
            this.close();
        }
    }

}
