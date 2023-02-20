package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public class CompoundTagRenderer implements TagRenderer {

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name, "(" + compoundTag.size() + ")", path, () -> {
            ContextMenu.start().allTypes(compoundTag::add).edit(name, compoundTag, nameEditConsumer, t -> {}).delete(deleteListener).render();
        }, () -> {
            String[] removed = new String[1];
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) {
                ImGuiImpl.getInstance().getMainWindow().renderNbt(newName -> {
                    //This gets executed multiple frames after the user clicked save in the popup
                    INbtTag oldTag = compoundTag.remove(entry.getKey());
                    compoundTag.add(newName, oldTag);
                }, () -> removed[0] = entry.getKey(), path + ">" + name, entry.getKey(), entry.getValue());
            }
            if (removed[0] != null) compoundTag.remove(removed[0]);
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
