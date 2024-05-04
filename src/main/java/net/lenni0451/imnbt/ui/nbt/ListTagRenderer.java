package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.imnbt.utils.nbt.TagTransformer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.ListTag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * The renderer for list tags.
 */
public class ListTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, INbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ListTag<INbtTag> listTag = (ListTag<INbtTag>) tag;
        this.renderBranch(name, "(" + listTag.getValue().size() + ")", path, () -> {
            this.renderIcon(drawer, NbtType.LIST);
            if (openContextMenu) {
                ContextMenu contextMenu = ContextMenu
                        .start(drawer, modificationListener)
                        .edit(name, listTag, nameEditConsumer, t -> {});
                if (listTag.isEmpty()) {
                    contextMenu.allTypes(0, (index, newTag) -> {
                        listTag.add(newTag);
                        searchProvider.refreshSearch();
                    });
                } else {
                    contextMenu.singleType(listTag.getType(), listTag.size(), (index, newTag) -> {
                        if (listTag.canAdd(newTag)) {
                            listTag.getValue().add(index, newTag);
                            searchProvider.refreshSearch();
                        } else {
                            throw new IllegalStateException("Invalid tag type: " + newTag.getNbtType().name());
                        }
                    });
                }
                contextMenu
                        .copy(name, listTag)
                        .paste((copiedName, copiedTag) -> {
                            if (!listTag.canAdd(copiedTag)) {
                                drawer.showNotification(NotificationLevel.ERROR, "Paste Tag", "This tag is not supported in this list tag.\n" + listTag.getNbtType().name() + " != " + copiedTag.getNbtType().name());
                            } else {
                                listTag.add(copiedTag);
                                searchProvider.refreshSearch();
                            }
                        })
                        .transform(TagTransformer.transform(drawer, name, listTag, transformListener), TagTransformer.LIST_TRANSFORMS)
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
            this.handleSearch(searchProvider, path);
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(listTag.size() / (float) drawer.getLinesPerPage());
            if (pages <= 1) {
                for (int i = 0; i < listTag.size(); i++) {
                    this.renderEntry(drawer, listTag, listTag.get(i), i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            } else {
                ImGui.text("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(-1);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                int searchPage = searchProvider.getOpenedPage(path);
                if (searchPage != -1) page[0] = searchPage;
                ImGui.sliderInt("##page " + path, page, 1, pages);

                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * drawer.getLinesPerPage();
                int end = Math.min(start + drawer.getLinesPerPage(), listTag.size());
                for (int i = start; i < end; i++) {
                    this.renderEntry(drawer, listTag, listTag.get(i), i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) {
                listTag.getValue().remove(removed[0]);
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
    }

    private void renderEntry(final ImNbtDrawer drawer, final ListTag<INbtTag> listTag, final INbtTag entry, final int i, final int[] removed, final Runnable modificationListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        NbtTreeRenderer.render(drawer, newName -> {
            //This gets executed multiple frames after the user clicked save in the popup
            try {
                int newIndex = Integer.parseInt(newName);
                if (newIndex < 0 || newIndex >= listTag.size() || newIndex == i) return;
                INbtTag oldTag = listTag.getValue().remove(i);
                listTag.getValue().add(newIndex, oldTag);
                searchProvider.refreshSearch();
            } catch (Throwable ignored) {
            }
        }, (transformedName, transformedTag) -> {
            int index = Integer.parseInt(transformedName);
            if (index < 0 || index >= listTag.size()) return;
            listTag.getValue().set(index, transformedTag);
            searchProvider.refreshSearch();
        }, () -> removed[0] = i, modificationListener, colorProvider, searchProvider, openContextMenu, get(path, i), String.valueOf(i), entry);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
