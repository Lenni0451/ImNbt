package net.lenni0451.imnbt.ui.nbt;

import imgui.ImGui;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.nbt.NbtPath.get;

public class CompoundTagRenderer implements TagRenderer {

    private final Map<String, int[]> pageCache = new HashMap<>();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, boolean openContextMenu, String path, String name, @Nonnull INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name, "(" + compoundTag.size() + ")", path, () -> {
            this.renderIcon(9);
            if (openContextMenu) {
                ContextMenu.start().allTypes(compoundTag::add).edit(name, compoundTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
            }
        }, () -> {
            String[] removed = new String[1];
            int pages = (int) Math.ceil(compoundTag.size() / (float) Main.LINES_PER_PAGE);
            if (pages <= 1) {
                for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) {
                    this.renderEntry(compoundTag, entry.getKey(), entry.getValue(), removed, colorProvider, openContextMenu, path);
                }
            } else {
                ImGui.text("Page");
                ImGui.sameLine();
                ImGui.setNextItemWidth(1 - 2);
                int[] page = this.pageCache.computeIfAbsent(path, p -> new int[]{1});
                ImGui.sliderInt("##page " + path, page, 1, pages);

                List<String> keys = new ArrayList<>(compoundTag.getValue().keySet());
                int start = (Math.max(1, Math.min(page[0], pages)) - 1) * Main.LINES_PER_PAGE;
                int end = Math.min(start + Main.LINES_PER_PAGE, keys.size());
                for (int i = start; i < end; i++) {
                    String key = keys.get(i);
                    this.renderEntry(compoundTag, key, compoundTag.get(key), removed, colorProvider, openContextMenu, path);
                }
            }
            if (removed[0] != null) compoundTag.remove(removed[0]);
        }, colorProvider);
    }

    private void renderEntry(final CompoundTag compoundTag, final String key, final INbtTag value, final String[] removed, final Function<String, Color> colorProvider, final boolean openContextMenu, final String path) {
        NbtTreeRenderer.render(newName -> {
            //This gets executed multiple frames after the user clicked save in the popup
            INbtTag oldTag = compoundTag.remove(key);
            compoundTag.add(newName, oldTag);
        }, () -> removed[0] = key, colorProvider, openContextMenu, get(path, key), key, value);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
