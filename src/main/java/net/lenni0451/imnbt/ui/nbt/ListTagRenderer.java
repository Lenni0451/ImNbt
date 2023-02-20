package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListTagRenderer implements TagRenderer {

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String name, @Nonnull INbtTag tag) {
        ListTag<INbtTag> listTag = (ListTag<INbtTag>) tag;
        this.renderBranch(name + " (" + listTag.getValue().size() + ")", tag.hashCode(), () -> {
            ContextMenu contextMenu = ContextMenu.start().edit(name, listTag, nameEditConsumer, t -> {});
            if (listTag.isEmpty()) contextMenu.allTypes((newName, newTag) -> listTag.add(newTag));
            else contextMenu.singleType(listTag.getType(), (newName, newTag) -> listTag.add(newTag));
            contextMenu.delete(deleteListener).render();
        }, () -> {
            List<INbtTag> removed = new ArrayList<>();
            for (int i = 0; i < listTag.getValue().size(); i++) {
                INbtTag listEntry = listTag.getValue().get(i);
                ImGuiImpl.getInstance().getMainWindow().renderNbt(newName -> {}, () -> removed.add(listEntry), String.valueOf(i), listEntry);
            }
            listTag.getValue().removeAll(removed);
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
