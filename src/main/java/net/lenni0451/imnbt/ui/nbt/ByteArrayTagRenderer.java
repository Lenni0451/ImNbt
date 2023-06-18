package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtNumber;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.ByteArrayTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.close;
import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

/**
 * The renderer for byte array tags.
 */
public class ByteArrayTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(ImNbtDrawer drawer, Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
        this.renderBranch(name, "(" + byteArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(drawer, 6);
            if (openContextMenu) {
                ContextMenu.start(drawer).singleType(NbtType.BYTE, (newName, newTag) -> {
                    int index = -1;
                    try {
                        int newIndex = Integer.parseInt(newName);
                        if (newIndex >= 0 && newIndex <= byteArrayTag.getLength()) index = newIndex;
                    } catch (Throwable ignored) {
                    }
                    if (index == -1) byteArrayTag.add(((ByteTag) newTag).getValue());
                    else byteArrayTag.setValue(ArrayUtils.insert(byteArrayTag.getValue(), index, ((ByteTag) newTag).getValue()));
                    searchProvider.refreshSearch();
                }).copy(byteArrayTag).paste(pastedTag -> {
                    if (pastedTag instanceof INbtNumber num) {
                        byteArrayTag.add(num.byteValue());
                        searchProvider.refreshSearch();
                    } else if (pastedTag instanceof ByteArrayTag bat) {
                        for (byte b : bat.getValue()) byteArrayTag.add(b);
                        searchProvider.refreshSearch();
                    } else {
                        drawer.openPopup(new MessagePopup("Invalid Tag", "You can only paste numbers into a byte array.", close(drawer)));
                    }
                }).edit(name, byteArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(byteArrayTag.getLength() / (float) drawer.getLinesPerPage());
            if (pages <= 1) {
                for (int i = 0; i < byteArrayTag.getLength(); i++) {
                    this.renderByte(drawer, byteArrayTag, i, removed, colorProvider, searchProvider, openContextMenu, path);
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
                int end = Math.min(start + drawer.getLinesPerPage(), byteArrayTag.getLength());
                for (int i = start; i < end; i++) {
                    this.renderByte(drawer, byteArrayTag, i, removed, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) {
                byteArrayTag.setValue(ArrayUtils.remove(byteArrayTag.getValue(), removed[0]));
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    private void renderByte(final ImNbtDrawer drawer, final ByteArrayTag byteArrayTag, final int index, final int[] removed, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        this.renderLeaf(String.valueOf(index), ": " + this.format.format(byteArrayTag.get(index)), get(path, index), () -> {
            this.renderIcon(drawer, 0);
            if (openContextMenu) {
                ContextMenu.start(drawer).edit(String.valueOf(index), new ByteTag(byteArrayTag.get(index)), newName -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    try {
                        int newIndex = Integer.parseInt(newName);
                        if (newIndex < 0 || newIndex >= byteArrayTag.getLength() || newIndex == index) return;
                        byte val = byteArrayTag.get(index);
                        byte[] newValue = ArrayUtils.remove(byteArrayTag.getValue(), index);
                        newValue = ArrayUtils.insert(newValue, newIndex, val);
                        byteArrayTag.setValue(newValue);
                        searchProvider.refreshSearch();
                    } catch (Throwable ignored) {
                    }
                }, newTag -> {
                    byteArrayTag.set(index, newTag.getValue());
                    searchProvider.refreshSearch();
                }).copy(new ByteTag(byteArrayTag.get(index))).delete(() -> removed[0] = index).render();
            }
        }, colorProvider, searchProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
