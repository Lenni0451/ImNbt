package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContextMenu {

    public static ContextMenu start() {
        return new ContextMenu();
    }


    private final Set<NbtType> newTypes = new LinkedHashSet<>();
    private Runnable editAction;
    private BiConsumer<String, INbtTag> newTagAction;
    private Runnable deleteListener;
    private Supplier<INbtTag> sNbtSerializerListener;

    public ContextMenu allTypes(final BiConsumer<String, INbtTag> newTagAction) {
        Collections.addAll(this.newTypes, NbtType.values());
        this.newTypes.remove(NbtType.END);
        this.newTagAction = newTagAction;
        return this;
    }

    public ContextMenu singleType(final NbtType newType, final BiConsumer<String, INbtTag> newTagAction) {
        this.newTypes.add(newType);
        this.newTagAction = newTagAction;
        return this;
    }

    public <T extends INbtTag> ContextMenu edit(final String name, final T tag, final Consumer<String> nameEditConsumer, final Consumer<T> tagConsumer) {
        this.editAction = () -> Main.getInstance().getMainWindow().openPopup(new EditTagPopup("Edit " + StringUtils.format(tag.getNbtType()), "Save", name, tag, (p, success) -> {
            if (success) {
                tagConsumer.accept((T) p.getTag());
                nameEditConsumer.accept(p.getName());
            }
            Main.getInstance().getMainWindow().closePopup();
        }));
        return this;
    }

    public ContextMenu delete(final Runnable deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public ContextMenu sNbtParser(final Supplier<INbtTag> sNbtSerializerListener) {
        this.sNbtSerializerListener = sNbtSerializerListener;
        return this;
    }

    public void render() {
        if (ImGui.beginPopupContextItem()) {
            if (!this.newTypes.isEmpty()) {
                if (ImGui.beginMenu("New")) {
                    for (NbtType newType : this.newTypes) {
                        if (ImGui.menuItem(StringUtils.format(newType))) {
                            Main.getInstance().getMainWindow().openPopup(new EditTagPopup("Add " + StringUtils.format(newType), "Add", "", newType.newInstance(), (p, success) -> {
                                if (success) this.newTagAction.accept(p.getName(), p.getTag());
                                Main.getInstance().getMainWindow().closePopup();
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
            if (this.deleteListener != null) {
                if (ImGui.menuItem("Delete")) {
                    this.deleteListener.run();
                }
            }
            if (this.sNbtSerializerListener != null) {
                if (ImGui.menuItem("SNbt Serializer")) {
                    Main.getInstance().getMainWindow().openPopup(new SNbtSerializerPopup(this.sNbtSerializerListener.get(), (p, success) -> Main.getInstance().getMainWindow().closePopup()));
                }
            }

            ImGui.endPopup();
        }
    }

}
