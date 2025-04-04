package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.NbtTag;

/**
 * A popup that allows the user to edit a tag.
 */
public class EditTagPopup extends Popup<EditTagPopup> {

    private final String okText;
    private final ImString name = new ImString(256);
    private final NbtTag tag;
    private final TagRenderer tagRenderer;

    public EditTagPopup(final String title, final String okText, final String name, final NbtTag tag, final PopupCallback<EditTagPopup> callback) {
        super(title, callback);

        this.okText = okText;
        this.name.set(name);
        this.tag = tag.copy();
        this.tagRenderer = NbtTreeRenderer.getTagRenderer(tag.getNbtType());
    }

    public String getName() {
        return this.name.get();
    }

    public NbtTag getTag() {
        return this.tag;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.inputText("Name", this.name);
        this.tagRenderer.renderValueEditor(this.tag);

        ImGui.separator();
        if (ImGui.button(this.okText)) {
            this.getCallback().onClose(this, true);
            this.close();
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            this.getCallback().onClose(this, false);
            this.close();
        }
    }

}
