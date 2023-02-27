package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.NbtPath.get;

public class CompoundTagRenderer implements TagRenderer {

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
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) {
                this.renderEntry(compoundTag, entry.getKey(), entry.getValue(), removed, colorProvider, openContextMenu, path);
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
