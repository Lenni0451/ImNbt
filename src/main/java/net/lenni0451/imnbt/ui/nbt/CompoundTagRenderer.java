package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import java.util.Map;

public class CompoundTagRenderer extends TagRenderer {

    @Override
    public void render(String name, INbtTag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        this.renderBranch(name + " (" + compoundTag.size() + ")", compoundTag.hashCode(), () -> {
            for (Map.Entry<String, INbtTag> entry : compoundTag.getValue().entrySet()) ImGuiImpl.getInstance().mainWindow.renderNbt(entry.getKey(), entry.getValue());
        });
    }

}
