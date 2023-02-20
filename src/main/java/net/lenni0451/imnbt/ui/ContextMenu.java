package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.popups.EditPopup;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ContextMenu {

    public static ContextMenu start() {
        return new ContextMenu();
    }


    private final Set<NbtType> newTypes = new HashSet<>();
    private BiConsumer<String, INbtTag> newTagAction;
    private Runnable editAction;
    private Runnable customAction;

    public ContextMenu edit(final Runnable editAction) {
        this.editAction = editAction;
        return this;
    }

    public <T extends INbtTag> ContextMenu edit(final String name, final T tag, final Consumer<String> nameEditConsumer, final Consumer<T> tagConsumer) {
        return this.edit(() -> ImGuiImpl.getInstance().getMainWindow().openPopup(new EditPopup("Edit " + StringUtils.format(tag.getNbtType()), name, tag, (p, success) -> {
            if (success) {
                nameEditConsumer.accept(p.getName());
                tagConsumer.accept((T) p.getTag());
            }
            ImGuiImpl.getInstance().getMainWindow().closePopup();
        })));
    }

    public ContextMenu allTypes(final BiConsumer<String, INbtTag> newTagAction) {
        Collections.addAll(this.newTypes, NbtType.values());
        this.newTypes.remove(NbtType.END);
        this.newTagAction = newTagAction;
        return this;
    }

    public ContextMenu newType(final NbtType newType, final BiConsumer<String, INbtTag> newTagAction) {
        this.newTypes.add(newType);
        this.newTagAction = newTagAction;
        return this;
    }

    public ContextMenu custom(final Runnable customAction) {
        this.customAction = customAction;
        return this;
    }

    public void render() {
        if (ImGui.beginPopupContextWindow()) {
            if (!this.newTypes.isEmpty()) {
                if (ImGui.beginMenu("New")) {
                    for (NbtType newType : this.newTypes) {
                        if (ImGui.menuItem(StringUtils.format(newType))) {
                            ImGuiImpl.getInstance().getMainWindow().openPopup(new EditPopup("Add " + StringUtils.format(newType), "", newType.newInstance(), (p, success) -> {
                                if (success) this.newTagAction.accept(p.getName(), p.getTag());
                                ImGuiImpl.getInstance().getMainWindow().closePopup();
                            }));
                        }
                    }

                    ImGui.endMenu();
                }
            }
            if (this.editAction != null) {
                if (ImGui.menuItem("Edit")) {
                    this.editAction.run();
                }
            }
            if (this.customAction != null) {
                this.customAction.run();
            }

            ImGui.endPopup();
        }
    }

}
