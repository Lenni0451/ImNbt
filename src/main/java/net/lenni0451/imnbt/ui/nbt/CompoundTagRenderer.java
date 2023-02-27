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
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, String path, String name, @Nonnull INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name, "(" + compoundTag.size() + ")", path, () -> {
            this.renderIcon(9);
            ContextMenu.start().allTypes(compoundTag::add).edit(name, compoundTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
        }, () -> {
            String[] removed = new String[1];
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) {
                NbtTreeRenderer.render(newName -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    INbtTag oldTag = compoundTag.remove(entry.getKey());
                    compoundTag.add(newName, oldTag);
                }, () -> removed[0] = entry.getKey(), colorProvider, get(path, entry.getKey()), entry.getKey(), entry.getValue());
            }
            if (removed[0] != null) compoundTag.remove(removed[0]);
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
