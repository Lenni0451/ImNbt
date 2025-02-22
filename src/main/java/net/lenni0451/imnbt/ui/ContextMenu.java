package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import imgui.ImVec2;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.popups.EditIndexedTagPopup;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.IntegerInputPopup;
import net.lenni0451.imnbt.ui.popups.YesNoPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtParserPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.io.NamedTag;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.*;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.close;

/**
 * A builder for the context menu when right clicking on a tag.
 */
public class ContextMenu {

    public static ContextMenu start(final ImNbtDrawer drawer, final Runnable modificationListener) {
        return new ContextMenu(drawer, modificationListener);
    }


    private final ImNbtDrawer drawer;
    private final Runnable modificationListener;
    private final Set<NbtType> newTypes = new LinkedHashSet<>();
    private String copyName;
    private INbtTag copyTag;
    private BiConsumer<String, INbtTag> pasteAction;
    private Runnable editAction;
    private Consumer<NbtType> transformAction;
    private IntConsumer roundAction;
    private Runnable sortAction;
    private NbtType[] transformTypes;
    private BiConsumer<String, INbtTag> newTagAction;
    private BiConsumer<Integer, INbtTag> intNewTagAction;
    private Predicate<String> overwriteCheck;
    private int newIndex;
    private Runnable deleteListener;
    private Supplier<INbtTag> sNbtSerializerListener;

    private ContextMenu(final ImNbtDrawer drawer, final Runnable modificationListener) {
        this.drawer = drawer;
        this.modificationListener = modificationListener;
    }

    /**
     * Allow all types to be created.
     *
     * @param newTagAction   The action to be executed when a new tag is created
     * @param overwriteCheck Check if a tag with the name already exists
     * @return The builder instance
     */
    public ContextMenu allTypes(final BiConsumer<String, INbtTag> newTagAction, final Predicate<String> overwriteCheck) {
        Collections.addAll(this.newTypes, NbtType.values());
        this.newTypes.remove(NbtType.END);
        this.newTagAction = newTagAction;
        this.overwriteCheck = overwriteCheck;
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
     * Allow all types to be created.
     *
     * @param newIndex     The index of the new tag
     * @param newTagAction The action to be executed when a new tag is created
     * @return The builder instance
     */
    public ContextMenu allTypes(final int newIndex, final BiConsumer<Integer, INbtTag> newTagAction) {
        Collections.addAll(this.newTypes, NbtType.values());
        this.newTypes.remove(NbtType.END);
        this.intNewTagAction = newTagAction;
        this.newIndex = newIndex;
        return this;
    }

    /**
     * Allow only a single type to be created.
     *
     * @param newType      The type to be created
     * @param newIndex     The index of the new tag
     * @param newTagAction The action to be executed when a new tag is created
     * @return The builder instance
     */
    public ContextMenu singleType(final NbtType newType, final int newIndex, final BiConsumer<Integer, INbtTag> newTagAction) {
        this.newTypes.add(newType);
        this.intNewTagAction = newTagAction;
        this.newIndex = newIndex;
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
     * Allow transforming the tag into another type.
     *
     * @param typeConsumer  The consumer to be executed when the tag is transformed
     * @param possibleTypes The possible types the tag can be transformed into
     * @return The builder instance
     */
    public ContextMenu transform(final Consumer<NbtType> typeConsumer, final NbtType... possibleTypes) {
        this.transformAction = typeConsumer;
        this.transformTypes = possibleTypes;
        return this;
    }

    /**
     * Allow rounding the tag to a specific amount of decimals.
     *
     * @param roundAction The action to be executed when the tag is rounded
     * @return The builder instance
     */
    public ContextMenu round(final IntConsumer roundAction) {
        this.roundAction = roundAction;
        return this;
    }

    /**
     * Allow sorting the tag.
     *
     * @param sortAction The action to be executed when the tag is sorted
     * @return The builder instance
     */
    public ContextMenu sort(final Runnable sortAction) {
        this.sortAction = sortAction;
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
                this.modificationListener.run();
                tagConsumer.accept((T) p.getTag());
                nameEditConsumer.accept(p.getName());
            }
            this.drawer.closePopup();
        }));
        return this;
    }

    /**
     * Allow editing the tag with an index.
     *
     * @param tag               The tag to be edited
     * @param index             The index of the tag
     * @param maxIndex          The maximum index of the tag
     * @param indexEditConsumer The consumer to be executed when the index is edited
     * @param tagConsumer       The consumer to be executed when the tag is edited
     * @param <T>               The type of the tag
     * @return The builder instance
     */
    public <T extends INbtTag> ContextMenu edit(final T tag, final int index, final int maxIndex, final IntConsumer indexEditConsumer, final Consumer<T> tagConsumer) {
        this.editAction = () -> this.drawer.openPopup(new EditIndexedTagPopup("Edit " + StringUtils.format(tag.getNbtType()), "Save", index, maxIndex, tag, (p, success) -> {
            if (success) {
                this.modificationListener.run();
                tagConsumer.accept((T) p.getTag());
                if (p.getIndex() != index) indexEditConsumer.accept(p.getIndex());
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
                            if (this.intNewTagAction == null) {
                                this.drawer.openPopup(new EditTagPopup("Add " + StringUtils.format(newType), "Add", "", newType.newInstance(), (p, success) -> {
                                    this.drawer.closePopup();
                                    if (success) {
                                        if (this.overwriteCheck != null && this.overwriteCheck.test(p.getName())) {
                                            this.drawer.openPopup(new YesNoPopup("Overwrite existing tag?", "A tag with this name already exists.\nDo you want to overwrite it?", (p2, success2) -> {
                                                if (success2) {
                                                    this.modificationListener.run();
                                                    this.newTagAction.accept(p.getName(), p.getTag());
                                                }
                                                this.drawer.closePopup();
                                            }));
                                        } else {
                                            this.modificationListener.run();
                                            this.newTagAction.accept(p.getName(), p.getTag());
                                        }
                                    }
                                }));
                            } else {
                                this.drawer.openPopup(new EditIndexedTagPopup("Add " + StringUtils.format(newType), "Add", this.newIndex, this.newIndex, newType.newInstance(), (p, success) -> {
                                    if (success) {
                                        this.modificationListener.run();
                                        this.intNewTagAction.accept(p.getIndex(), p.getTag());
                                    }
                                    this.drawer.closePopup();
                                }));
                            }
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
                        this.drawer.showNotification(NotificationLevel.ERROR, "Paste Error", "An unknown error occurred whilst\npasting the clipboard content!");
                    } else {
                        this.modificationListener.run();
                        this.pasteAction.accept(tag.getName(), tag.getTag().copy());
                    }
                }
                String clipboardString = ImGui.getClipboardText();
                if (clipboardString != null && !clipboardString.isBlank() && ImGui.menuItem("Paste SNbt")) {
                    this.drawer.openPopup(new SNbtParserPopup(clipboardString, (p, success) -> {
                        if (success) {
                            this.drawer.openPopup(new EditTagPopup("Edit deserialized SNbt", "Paste", "", p.getParsedTag(), (p2, success2) -> {
                                if (success2) {
                                    this.modificationListener.run();
                                    this.pasteAction.accept(p2.getName(), p2.getTag());
                                }
                                this.drawer.closePopup();
                            }));
                        } else {
                            this.drawer.closePopup();
                        }
                    }));
                }
            }
            if (this.transformAction != null && this.transformTypes.length > 0) {
                if (ImGui.beginMenu("Transform")) {
                    for (NbtType type : this.transformTypes) {
                        if (ImGui.menuItem("     " + StringUtils.format(type))) {
                            this.modificationListener.run();
                            this.transformAction.accept(type);
                        }
                        ImVec2 xy = ImGui.getItemRectMin();
                        xy.x++;
                        xy.y += 2;
                        NbtTreeRenderer.renderIcon(this.drawer, xy, type);
                    }

                    ImGui.endMenu();
                }
            }
            if (this.roundAction != null) {
                if (ImGui.menuItem("Round")) {
                    this.drawer.openPopup(new IntegerInputPopup("Round", "Enter the amount of decimals to round to", 0, 10, 2, i -> {
                        this.modificationListener.run();
                        this.roundAction.accept(i);
                    }, close(this.drawer)));
                }
            }
            if (this.sortAction != null) {
                if (ImGui.menuItem("Sort")) {
                    this.modificationListener.run();
                    this.sortAction.run();
                }
            }
            if (this.editAction != null) {
                if (ImGui.menuItem("Edit")) {
                    this.editAction.run();
                }
            }
            if (this.deleteListener != null) {
                if (ImGui.menuItem("Delete")) {
                    this.modificationListener.run();
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
