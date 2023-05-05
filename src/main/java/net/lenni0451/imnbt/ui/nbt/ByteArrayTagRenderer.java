package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.imnbt.utils.Color;
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

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

public class ByteArrayTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, SearchProvider searchProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
        this.renderBranch(name, "(" + byteArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(6);
            if (openContextMenu) {
                ContextMenu.start().singleType(NbtType.BYTE, (newName, newTag) -> {
                    int index = -1;
                    try {
                        int newIndex = Integer.parseInt(newName);
                        if (newIndex >= 0 && newIndex <= byteArrayTag.getLength()) index = newIndex;
                    } catch (Throwable ignored) {
                    }
                    if (index == -1) byteArrayTag.add(((ByteTag) newTag).getValue());
                    else byteArrayTag.setValue(ArrayUtils.insert(byteArrayTag.getValue(), index, ((ByteTag) newTag).getValue()));
                    searchProvider.refreshSearch();
                }).edit(name, byteArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(byteArrayTag.getLength() / (float) Main.LINES_PER_PAGE);
            if (pages <= 1) {
                for (int i = 0; i < byteArrayTag.getLength(); i++) {
                    this.renderByte(byteArrayTag, i, removed, colorProvider, searchProvider, openContextMenu, path);
                }
            } else {
                ImGui.text("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(-1);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                int searchPage = searchProvider.getOpenedPage(path);
                if (searchPage != -1) page[0] = searchPage;
                ImGui.sliderInt("##page " + path, page, 1, pages);

                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * Main.LINES_PER_PAGE;
                int end = Math.min(start + Main.LINES_PER_PAGE, byteArrayTag.getLength());
                for (int i = start; i < end; i++) {
                    this.renderByte(byteArrayTag, i, removed, colorProvider, searchProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) {
                byteArrayTag.setValue(ArrayUtils.remove(byteArrayTag.getValue(), removed[0]));
                searchProvider.refreshSearch();
            }
        }, colorProvider, searchProvider);
        this.handleSearch(searchProvider, path);
    }

    private void renderByte(final ByteArrayTag byteArrayTag, final int index, final int[] removed, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path) {
        this.renderLeaf(String.valueOf(index), ": " + this.format.format(byteArrayTag.get(index)), get(path, index), () -> {
            this.renderIcon(0);
            if (openContextMenu) {
                ContextMenu.start().edit(String.valueOf(index), new ByteTag(byteArrayTag.get(index)), newName -> {
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
                }).delete(() -> removed[0] = index).render();
            }
        }, colorProvider, searchProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
