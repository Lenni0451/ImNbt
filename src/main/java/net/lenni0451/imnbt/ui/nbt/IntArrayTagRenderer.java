package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.imnbt.utils.nbt.TagTransformer;
import net.lenni0451.mcstructs.nbt.NbtNumber;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.IntArrayTag;
import net.lenni0451.mcstructs.nbt.tags.IntTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * The renderer for int array tags.
 */
public class IntArrayTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, NbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull NbtTag tag) {
        IntArrayTag intArrayTag = (IntArrayTag) tag;
        this.renderBranch(name, "(" + intArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(drawer, NbtType.INT_ARRAY);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .singleType(NbtType.INT, intArrayTag.getLength(), (index, newTag) -> {
                            if (index == intArrayTag.getLength()) intArrayTag.add(((IntTag) newTag).getValue());
                            else intArrayTag.setValue(ArrayUtils.insert(intArrayTag.getValue(), index, ((IntTag) newTag).getValue()));
                            searchProvider.refreshSearch();
                        })
                        .copy(name, intArrayTag).paste((pastedName, pastedTag) -> {
                            if (pastedTag instanceof NbtNumber num) {
                                intArrayTag.add(num.intValue());
                                searchProvider.refreshSearch();
                            } else if (pastedTag instanceof IntArrayTag iat) {
                                for (int i : iat.getValue()) intArrayTag.add(i);
                                searchProvider.refreshSearch();
                            } else {
                                drawer.showNotification(NotificationLevel.ERROR, "Invalid Tag", "You can only paste numbers into an int array.");
                            }
                        })
                        .transform(TagTransformer.transform(drawer, name, intArrayTag, transformListener), TagTransformer.INT_ARRAY_TRANSFORMS)
                        .edit(name, intArrayTag, nameEditConsumer, t -> {})
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(intArrayTag.getLength() / (float) drawer.getLinesPerPage());
            if (pages <= 1) {
                for (int i = 0; i < intArrayTag.getLength(); i++) {
                    this.renderInt(drawer, intArrayTag, i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            } else {
                ImGui.textUnformatted("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(-1);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                int searchPage = searchProvider.getOpenedPage(path);
                if (searchPage != -1) page[0] = searchPage;
                ImGui.sliderInt("##page " + path, page, 1, pages);

                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * drawer.getLinesPerPage();
                int end = Math.min(start + drawer.getLinesPerPage(), intArrayTag.getLength());
                for (int i = start; i < end; i++) {
                    this.renderInt(drawer, intArrayTag, i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) {
                intArrayTag.setValue(ArrayUtils.remove(intArrayTag.getValue(), removed[0]));
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    private void renderInt(final ImNbtDrawer drawer, final IntArrayTag intArrayTag, final int index, final int[] removed, final Runnable modificationListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        this.renderLeaf(String.valueOf(index), ": " + this.format.format(intArrayTag.get(index)), get(path, index), () -> {
            this.renderIcon(drawer, NbtType.INT);
            if (openContextMenu) {
                ContextMenu.start(drawer, modificationListener).edit(new IntTag(intArrayTag.get(index)), index, intArrayTag.getLength() - 1, newIndex -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    int val = intArrayTag.get(index);
                    int[] newValue = ArrayUtils.remove(intArrayTag.getValue(), index);
                    newValue = ArrayUtils.insert(newValue, newIndex, val);
                    intArrayTag.setValue(newValue);
                    searchProvider.refreshSearch();
                }, newTag -> {
                    intArrayTag.set(index, newTag.getValue());
                    searchProvider.refreshSearch();
                }).copy(String.valueOf(index), new IntTag(intArrayTag.get(index))).delete(() -> removed[0] = index).render();
            }
        }, colorProvider, searchProvider);
    }

    @Override
    public void renderValueEditor(NbtTag tag) {
    }

}
