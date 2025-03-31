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
import net.lenni0451.mcstructs.nbt.tags.ByteArrayTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * The renderer for byte array tags.
 */
public class ByteArrayTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, BiConsumer<String, NbtTag> transformListener, Runnable deleteListener, Runnable modificationListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull NbtTag tag) {
        ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
        this.renderBranch(name, "(" + byteArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(drawer, NbtType.BYTE_ARRAY);
            if (openContextMenu) {
                ContextMenu
                        .start(drawer, modificationListener)
                        .singleType(NbtType.BYTE, byteArrayTag.getLength(), (index, newTag) -> {
                            if (index == byteArrayTag.getLength()) byteArrayTag.add(((ByteTag) newTag).getValue());
                            else byteArrayTag.setValue(ArrayUtils.insert(byteArrayTag.getValue(), index, ((ByteTag) newTag).getValue()));
                            searchProvider.refreshSearch();
                        })
                        .copy(name, byteArrayTag).paste((pastedName, pastedTag) -> {
                            if (pastedTag instanceof NbtNumber num) {
                                byteArrayTag.add(num.byteValue());
                                searchProvider.refreshSearch();
                            } else if (pastedTag instanceof ByteArrayTag bat) {
                                for (byte b : bat.getValue()) byteArrayTag.add(b);
                                searchProvider.refreshSearch();
                            } else {
                                drawer.showNotification(NotificationLevel.ERROR, "Invalid Tag", "You can only paste numbers into a byte array.");
                            }
                        })
                        .transform(TagTransformer.transform(drawer, name, byteArrayTag, transformListener), TagTransformer.BYTE_ARRAY_TRANSFORMS)
                        .edit(name, byteArrayTag, nameEditConsumer, t -> {})
                        .delete(deleteListener)
                        .sNbtParser(() -> tag)
                        .render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(byteArrayTag.getLength() / (float) drawer.getLinesPerPage());
            if (pages <= 1) {
                for (int i = 0; i < byteArrayTag.getLength(); i++) {
                    this.renderByte(drawer, byteArrayTag, i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
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
                int end = Math.min(start + drawer.getLinesPerPage(), byteArrayTag.getLength());
                for (int i = start; i < end; i++) {
                    this.renderByte(drawer, byteArrayTag, i, removed, modificationListener, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) {
                byteArrayTag.setValue(ArrayUtils.remove(byteArrayTag.getValue(), removed[0]));
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    private void renderByte(final ImNbtDrawer drawer, final ByteArrayTag byteArrayTag, final int index, final int[] removed, final Runnable modificationListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        this.renderLeaf(String.valueOf(index), ": " + this.format.format(byteArrayTag.get(index)), get(path, index), () -> {
            this.renderIcon(drawer, NbtType.BYTE);
            if (openContextMenu) {
                ContextMenu.start(drawer, modificationListener).edit(new ByteTag(byteArrayTag.get(index)), index, byteArrayTag.getLength() - 1, newIndex -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    byte val = byteArrayTag.get(index);
                    byte[] newValue = ArrayUtils.remove(byteArrayTag.getValue(), index);
                    newValue = ArrayUtils.insert(newValue, newIndex, val);
                    byteArrayTag.setValue(newValue);
                    searchProvider.refreshSearch();
                }, newTag -> {
                    byteArrayTag.set(index, newTag.getValue());
                    searchProvider.refreshSearch();
                }).copy(String.valueOf(index), new ByteTag(byteArrayTag.get(index))).delete(() -> removed[0] = index).render();
            }
        }, colorProvider, searchProvider);
    }

    @Override
    public void renderValueEditor(NbtTag tag) {
    }

}
