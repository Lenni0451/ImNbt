package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import imgui.ImVec2;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.io.NamedTag;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.close;

/**
 * A builder for the context menu when right clicking on a tag.
 */
public class ContextMenu {

    public static ContextMenu start(final ImNbtDrawer drawer) {
        return new ContextMenu(drawer);
    }


    private final ImNbtDrawer drawer;
    private final Set<NbtType> newTypes = new LinkedHashSet<>();
    private String copyName;
    private INbtTag copyTag;
    private BiConsumer<String, INbtTag> pasteAction;
    private Runnable editAction;
    private BiConsumer<String, INbtTag> newTagAction;
    private Runnable deleteListener;
    private Supplier<INbtTag> sNbtSerializerListener;

    private ContextMenu(final ImNbtDrawer drawer) {
        this.drawer = drawer;
    }

    /**
     * Allow all types to be created.
     *
     * @param newTagAction The action to be executed when a new tag is created
     * @return The builder instance
     */
    public ContextMenu allTypes(final BiConsumer<String, INbtTag> newTagAction) {
        Collections.addAll(this.newTypes, NbtType.values());
        this.newTypes.remove(NbtType.END);
        this.newTagAction = newTagAction;
        return this;
    }

    /**
     * Allow only a single type to be created.
     *
     * @param newType      The type to be created
     * @param newTagAction The action to be executed when a new tag is created
     * @return The builder instance
     */
    public ContextMenu singleType(final NbtType newType, final BiConsumer<String, INbtTag> newTagAction) {
        this.newTypes.add(newType);
        this.newTagAction = newTagAction;
        return this;
    }

    /**
     * Allow the tag to be copied.<br>
     * The tag is cloned before it is copied.
     *
     * @param tag The tag to be copied
     * @return The builder instance
     */
    public ContextMenu copy(final String name, final INbtTag tag) {
        this.copyName = name;
        this.copyTag = tag;
        return this;
    }

    /**
     * Allow the clipboard to be pasted.
     *
     * @param pasteAction The action to be executed when the tag is pasted
     * @return The builder instance
     */
    public ContextMenu paste(final BiConsumer<String, INbtTag> pasteAction) {
        this.pasteAction = pasteAction;
        return this;
    }

    /**
     * Allow editing the tag.
     *
     * @param name             The name of the tag
     * @param tag              The tag to be edited
     * @param nameEditConsumer The consumer to be executed when the name is edited
     * @param tagConsumer      The consumer to be executed when the tag is edited
     * @param <T>              The type of the tag
     * @return The builder instance
     */
    public <T extends INbtTag> ContextMenu edit(final String name, final T tag, final Consumer<String> nameEditConsumer, final Consumer<T> tagConsumer) {
        this.editAction = () -> this.drawer.openPopup(new EditTagPopup("Edit " + StringUtils.format(tag.getNbtType()), "Save", name, tag, (p, success) -> {
            if (success) {
                tagConsumer.accept((T) p.getTag());
                nameEditConsumer.accept(p.getName());
            }
            this.drawer.closePopup();
        }));
        return this;
    }

    /**
     * Allow a tag to be deleted.
     *
     * @param deleteListener The listener to be executed when the tag is deleted
     * @return The builder instance
     */
    public ContextMenu delete(final Runnable deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    /**
     * Allow a tag to be serialized to SNbt.
     *
     * @param sNbtSerializerListener The listener to be executed when the tag is serialized
     * @return The builder instance
     */
    public ContextMenu sNbtParser(final Supplier<INbtTag> sNbtSerializerListener) {
        this.sNbtSerializerListener = sNbtSerializerListener;
        return this;
    }

    /**
     * Render the context menu and handle the actions.
     */
    public void render() {
        if (ImGui.beginPopupContextItem()) {
            if (!this.newTypes.isEmpty()) {
                if (ImGui.beginMenu("New")) {
                    for (NbtType newType : this.newTypes) {
                        if (ImGui.menuItem("     " + StringUtils.format(newType))) {
                            this.drawer.openPopup(new EditTagPopup("Add " + StringUtils.format(newType), "Add", "", newType.newInstance(), (p, success) -> {
                                if (success) this.newTagAction.accept(p.getName(), p.getTag());
                                this.drawer.closePopup();
                            }));
                        }
                        ImVec2 xy = ImGui.getItemRectMin();
                        xy.x++;
                        xy.y += 2;
                        NbtTreeRenderer.renderIcon(this.drawer, xy, newType);
                    }

                    ImGui.endMenu();
                }
            }
            if (this.copyTag != null) {
                if (ImGui.menuItem("Copy Tag")) {
                    this.drawer.setClipboard(new NamedTag(this.copyName, this.copyTag.getNbtType(), this.copyTag.copy()));
                }
            }
            if (this.pasteAction != null) {
                if (this.drawer.hasClipboard() && ImGui.menuItem("Paste Tag")) {
                    NamedTag tag = this.drawer.getClipboard();
                    if (tag == null) {
                        this.drawer.openPopup(new MessagePopup("Paste Error", "An unknown error occurred whilst\npasting the clipboard content!", close(this.drawer)));
                    } else {
                        this.pasteAction.accept(tag.getName(), tag.getTag());
                    }
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
                    this.drawer.openPopup(new SNbtSerializerPopup(this.sNbtSerializerListener.get(), close(this.drawer)));
                }
            }

            ImGui.endPopup();
        }
    }

}
