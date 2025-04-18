package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.nbt.TagSorter;
import net.lenni0451.imnbt.utils.nbt.TagTransformer;
import net.lenni0451.imnbt.utils.nbt.TagUtils;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * The renderer for compound tags.
 */
public class CompoundTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, NbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull NbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name, "(" + compoundTag.size() + ")", path, () -> {
            this.renderIcon(drawer, NbtType.COMPOUND);
            this.handleSearch(searchProvider, path);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .allTypes((newKey, newTag) -> {
                            compoundTag.add(newKey, newTag);
                            searchProvider.refreshSearch();
                        }, compoundTag::contains)
                        .copy(name, compoundTag).paste((copiedName, copiedTag) -> {
                            compoundTag.add(TagUtils.findUniqueName(compoundTag, copiedName), copiedTag);
                            searchProvider.refreshSearch();
                        })
                        .transform(TagTransformer.transform(drawer, name, compoundTag, transformListener), TagTransformer.COMPOUND_TRANSFORMS)
                        .sort(() -> TagSorter.sort(compoundTag))
                        .edit(name, compoundTag, nameEditConsumer, t -> {})
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, () -> {
            String[] removed = new String[1];
            int pages = (int) Math.ceil(compoundTag.size() / (float) drawer.getLinesPerPage());
            if (pages <= 1) {
                for (Map.Entry<String, NbtTag> entry : compoundTag.getValue().entrySet()) {
                    this.renderEntry(drawer, compoundTag, entry.getKey(), entry.getValue(), removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            } else {
                ImGui.textUnformatted("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(-1);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                int searchPage = searchProvider.getOpenedPage(path);
                if (searchPage != -1) page[0] = searchPage;
                ImGui.sliderInt("##page " + path, page, 1, pages);

                List<String> keys = new ArrayList<>(compoundTag.getValue().keySet());
                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * drawer.getLinesPerPage();
                int end = Math.min(start + drawer.getLinesPerPage(), keys.size());
                for (int i = start; i < end; i++) {
                    String key = keys.get(i);
                    this.renderEntry(drawer, compoundTag, key, compoundTag.get(key), removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != null) {
                compoundTag.remove(removed[0]);
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
    }

    private void renderEntry(final ImNbtDrawer drawer, final CompoundTag compoundTag, final String key, final NbtTag value, final String[] removed, final Runnable modificationListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        NbtTreeRenderer.render(drawer, newName -> {
            //This gets executed multiple frames after the user clicked save in the popup
            NbtTag oldTag = compoundTag.remove(key);
            compoundTag.add(newName, oldTag);
            searchProvider.refreshSearch();
        }, (transformedName, transformedTag) -> {
            compoundTag.add(transformedName, transformedTag);
            searchProvider.refreshSearch();
        }, () -> removed[0] = key, modificationListener, colorProvider, searchProvider, openContextMenu, get(path, key), key, value);
    }

    @Override
    public void renderValueEditor(NbtTag tag) {
    }

}
