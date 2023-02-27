package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.NbtPath.get;

public class ListTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ListTag<INbtTag> listTag = (ListTag<INbtTag>) tag;
        this.renderBranch(name, "(" + listTag.getValue().size() + ")", path, () -> {
            this.renderIcon(8);
            if (openContextMenu) {
                ContextMenu contextMenu = ContextMenu.start().edit(name, listTag, nameEditConsumer, t -> {});
                if (listTag.isEmpty()) contextMenu.allTypes((newName, newTag) -> listTag.add(newTag));
                else contextMenu.singleType(listTag.getType(), (newName, newTag) -> listTag.add(newTag));
                contextMenu.delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(listTag.size() / (float) Main.LINES_PER_PAGE);
            if (pages <= 1) {
                for (int i = 0; i < listTag.size(); i++) {
                    this.renderEntry(listTag, listTag.get(i), i, removed, colorProvider, openContextMenu, path);
                }
            } else {
                ImGui.text("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(1 - 2);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                ImGui.sliderInt("##page " + path, page, 1, pages);

                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * Main.LINES_PER_PAGE;
                int end = Math.min(start + Main.LINES_PER_PAGE, listTag.size());
                for (int i = start; i < end; i++) {
                    this.renderEntry(listTag, listTag.get(i), i, removed, colorProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) listTag.getValue().remove(removed[0]);
        }, colorProvider);
    }

    private void renderEntry(final ListTag<INbtTag> listTag, final INbtTag entry, final int i, final int[] removed, final Function<String, Color> colorProvider, final boolean openContextMenu, final String path) {
        NbtTreeRenderer.render(newName -> {
            //This gets executed multiple frames after the user clicked save in the popup
            try {
                int newIndex = Integer.parseInt(newName);
                if (newIndex < 0 || newIndex >= listTag.size() || newIndex == i) return;
                INbtTag oldTag = listTag.getValue().remove(i);
                listTag.getValue().add(newIndex, oldTag);
            } catch (Throwable ignored) {
            }
        }, () -> removed[0] = i, colorProvider, openContextMenu, get(path, i), String.valueOf(i), entry);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
