package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.LongArrayTag;
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.NbtPath.get;

public class LongArrayTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        LongArrayTag longArrayTag = (LongArrayTag) tag;
        this.renderBranch(name, "(" + longArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(11);
            if (openContextMenu) {
                ContextMenu.start().singleType(NbtType.LONG, (newName, newTag) -> {
                    int index = -1;
                    try {
                        int newIndex = Integer.parseInt(newName);
                        if (newIndex >= 0 && newIndex <= longArrayTag.getLength()) index = newIndex;
                    } catch (Throwable ignored) {
                    }
                    if (index == -1) longArrayTag.add(((LongTag) newTag).getValue());
                    else longArrayTag.setValue(ArrayUtils.insert(longArrayTag.getValue(), index, ((LongTag) newTag).getValue()));
                }).edit(name, longArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, () -> {
            int[] removed = new int[]{-1};
            int pages = (int) Math.ceil(longArrayTag.getLength() / (float) Main.LINES_PER_PAGE);
            if (pages == 1) {
                for (int i = 0; i < longArrayTag.getLength(); i++) {
                    this.renderLong(longArrayTag, i, removed, colorProvider, openContextMenu, path);
                }
            } else {
                ImGui.text("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(1 - 2);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                ImGui.sliderInt("##page " + path, page, 1, pages);

                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * Main.LINES_PER_PAGE;
                int end = Math.min(start + Main.LINES_PER_PAGE, longArrayTag.getLength());
                for (int i = start; i < end; i++) {
                    this.renderLong(longArrayTag, i, removed, colorProvider, openContextMenu, path);
                }
            }
            if (removed[0] != -1) longArrayTag.setValue(ArrayUtils.remove(longArrayTag.getValue(), removed[0]));
        }, colorProvider);
    }

    private void renderLong(final LongArrayTag longArrayTag, final int index, final int[] removed, final Function<String, Color> colorProvider, final boolean openContextMenu, final String path) {
        this.renderLeaf(String.valueOf(index), ": " + this.format.format(longArrayTag.get(index)), get(path, index), () -> {
            this.renderIcon(3);
            if (openContextMenu) {
                ContextMenu.start().edit(String.valueOf(index), new LongTag(longArrayTag.get(index)), newName -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    try {
                        int newIndex = Integer.parseInt(newName);
                        if (newIndex < 0 || newIndex >= longArrayTag.getLength() || newIndex == index) return;
                        long val = longArrayTag.get(index);
                        long[] newValue = ArrayUtils.remove(longArrayTag.getValue(), index);
                        newValue = ArrayUtils.insert(newValue, newIndex, val);
                        longArrayTag.setValue(newValue);
                    } catch (Throwable ignored) {
                    }
                }, newTag -> longArrayTag.set(index, newTag.getValue())).delete(() -> removed[0] = index).render();
            }
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
