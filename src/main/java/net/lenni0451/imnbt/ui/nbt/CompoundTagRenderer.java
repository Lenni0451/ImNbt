package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Map;

public class CompoundTagRenderer implements TagRenderer {

    @Override
    public void render(String name, @Nonnull INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name + " (" + compoundTag.size() + ")", compoundTag.hashCode(), () -> {
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) ImGuiImpl.getInstance().mainWindow.renderNbt(entry.getKey(), entry.getValue());
        });
    }

}
