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
import net.lenni0451.mcstructs.nbt.tags.LongArrayTag;
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * The renderer for long array tags.
 */
public class LongArrayTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, NbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull NbtTag tag) {
        LongArrayTag longArrayTag = (LongArrayTag) tag;
        this.renderBranch(name, "(" + longArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(drawer, NbtType.LONG_ARRAY);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .singleType(NbtType.LONG, longArrayTag.getLength(), (index, newTag) -> {
                            if (index == longArrayTag.getLength()) longArrayTag.add(((LongTag) newTag).getValue());
                            else longArrayTag.setValue(ArrayUtils.insert(longArrayTag.getValue(), index, ((LongTag) newTag).getValue()));
                            searchProvider.refreshSearch();
                        })
                        .copy(name, longArrayTag).paste((pastedName, pastedTag) -> {
                            if (pastedTag instanceof NbtNumber num) {
                                longArrayTag.add(num.longValue());
                                searchProvider.refreshSearch();
                            } else if (pastedTag instanceof LongArrayTag iat) {
                                for (long l : iat.getValue()) longArrayTag.add(l);
                                searchProvider.refreshSearch();
                            } else {
                                drawer.showNotification(NotificationLevel.ERROR, "Invalid Tag", "You can only paste numbers into a long array.");
                            }
                        })
                        .transform(TagTransformer.transform(drawer, name, longArrayTag, transformListener), TagTransformer.LONG_ARRAY_TRANSFORMS)
                        .edit(name, longArrayTag, nameEditConsumer, t -> {})
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(longArrayTag.getLength() / (float) drawer.getLinesPerPage());
            if (pages <= 1) {
                for (int i = 0; i < longArrayTag.getLength(); i++) {
                    this.renderLong(drawer, longArrayTag, i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
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
                int end = Math.min(start + drawer.getLinesPerPage(), longArrayTag.getLength());
                for (int i = start; i < end; i++) {
                    this.renderLong(drawer, longArrayTag, i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) {
                longArrayTag.setValue(ArrayUtils.remove(longArrayTag.getValue(), removed[0]));
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    private void renderLong(final ImNbtDrawer drawer, final LongArrayTag longArrayTag, final int index, final int[] removed, final Runnable modificationListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        this.renderLeaf(String.valueOf(index), ": " + this.format.format(longArrayTag.get(index)), get(path, index), () -> {
            this.renderIcon(drawer, NbtType.LONG);
            if (openContextMenu) {
                ContextMenu.start(drawer, modificationListener).edit(new LongTag(longArrayTag.get(index)), index, longArrayTag.getLength() - 1, newIndex -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    long val = longArrayTag.get(index);
                    long[] newValue = ArrayUtils.remove(longArrayTag.getValue(), index);
                    newValue = ArrayUtils.insert(newValue, newIndex, val);
                    longArrayTag.setValue(newValue);
                    searchProvider.refreshSearch();
                }, newTag -> {
                    longArrayTag.set(index, newTag.getValue());
                    searchProvider.refreshSearch();
                }).copy(String.valueOf(index), new LongTag(longArrayTag.get(index))).delete(() -> removed[0] = index).render();
            }
        }, colorProvider, searchProvider);
    }

    @Override
    public void renderValueEditor(NbtTag tag) {
    }

}
