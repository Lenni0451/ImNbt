package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ListTagRenderer implements TagRenderer {

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        ListTag<INbtTag> listTag = (ListTag<INbtTag>) tag;
        this.renderBranch(name, "(" + listTag.getValue().size() + ")", path, () -> {
            this.renderIcon(8);
            ContextMenu contextMenu = ContextMenu.start().edit(name, listTag, nameEditConsumer, t -> {});
            if (listTag.isEmpty()) contextMenu.allTypes((newName, newTag) -> listTag.add(newTag));
            else contextMenu.singleType(listTag.getType(), (newName, newTag) -> listTag.add(newTag));
            contextMenu.delete(deleteListener).render();
        }, () -> {
            int[] removed = new int[]{-1};
            for (int i = 0; i < listTag.size(); i++) {
                final int fi = i;
                INbtTag listEntry = listTag.get(i);
                ImGuiImpl.getInstance().getMainWindow().renderNbt(newName -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    try {
                        int newIndex = Integer.parseInt(newName);
                        if (newIndex < 0 || newIndex >= listTag.size() || newIndex == fi) return;
                        INbtTag oldTag = listTag.getValue().remove(fi);
                        listTag.getValue().add(newIndex, oldTag);
                    } catch (Throwable ignored) {
                    }
                }, () -> removed[0] = fi, path + ">" + name, String.valueOf(i), listEntry);
            }
            if (removed[0] != -1) listTag.getValue().remove(removed[0]);
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
