package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

public class ListTagRenderer implements TagRenderer {

    @Override
    public void render(String name, INbtTag tag) {
        ListTag<INbtTag> listTag = (ListTag<INbtTag>) tag;
        this.renderBranch(name + " (" + listTag.getValue().size() + ")", tag.hashCode(), () -> {
            for (int i = 0; i < listTag.getValue().size(); i++) {
                INbtTag listEntry = listTag.getValue().get(i);
                ImGuiImpl.getInstance().mainWindow.renderNbt(String.valueOf(i), listEntry);
            }
        });
    }

}
