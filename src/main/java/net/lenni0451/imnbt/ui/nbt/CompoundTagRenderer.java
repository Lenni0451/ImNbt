package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CompoundTagRenderer implements TagRenderer {

    @Override
    public void render(Consumer<String> nameEditConsumer, String name, @Nonnull INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name + " (" + compoundTag.size() + ")", compoundTag.hashCode(), () -> {
            ContextMenu.start().allTypes(compoundTag::add).edit(name, compoundTag, nameEditConsumer, t -> {}).render();

            Map<INbtTag, String> changedNames = new HashMap<>();
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) {
                ImGuiImpl.getInstance().getMainWindow().renderNbt(newName -> changedNames.put(entry.getValue(), newName), entry.getKey(), entry.getValue());
            }
            for (Map.Entry<INbtTag, String> entry : changedNames.entrySet()) {
                compoundTag.getValue().values().remove(entry.getKey());
                compoundTag.getValue().put(entry.getValue(), entry.getKey());
            }
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
