package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.NbtTag;

/**
 * A popup that allows the user to edit a tag.
 */
public class EditIndexedTagPopup extends Popup<EditIndexedTagPopup> {

    private final String okText;
    private final int[] index;
    private final int maxConfig;
    private final NbtTag tag;
    private final TagRenderer tagRenderer;

    public EditIndexedTagPopup(final String title, final String okText, final int index, final int maxIndex, final NbtTag tag, final PopupCallback<EditIndexedTagPopup> callback) {
        super(title, callback);

        this.okText = okText;
        this.index = new int[]{index};
        this.maxConfig = maxIndex;
        this.tag = tag.copy();
        this.tagRenderer = NbtTreeRenderer.getTagRenderer(tag.getNbtType());
    }

    public int getIndex() {
        return this.index[0];
    }

    public NbtTag getTag() {
        return this.tag;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.sliderInt("Index", this.index, 0, this.maxConfig);
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
