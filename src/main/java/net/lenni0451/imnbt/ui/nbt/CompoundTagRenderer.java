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
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String name, @Nonnull INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name + " (" + compoundTag.size() + ")", compoundTag.hashCode(), () -> {
            ContextMenu.start().allTypes(compoundTag::add).edit(name, compoundTag, nameEditConsumer, t -> {}).delete(deleteListener).render();
        }, () -> {
            String[] change = new String[2];
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) {
                ImGuiImpl.getInstance().getMainWindow().renderNbt(newName -> {
                    change[0] = entry.getKey();
                    change[1] = newName;
                }, () -> {
                    change[0] = entry.getKey();
                }, entry.getKey(), entry.getValue());
            }
            if (change[0] != null) {
                INbtTag oldValue = compoundTag.remove(change[0]);
                if (change[1] != null) compoundTag.add(change[1], oldValue);
            }
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
