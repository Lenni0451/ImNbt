package net.lenni0451.imnbt.ui.windows;

import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.popups.file.OpenFilePopup;
import net.lenni0451.imnbt.ui.popups.file.SaveFilePopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtParserPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.imgui.FileDialogs;
import net.lenni0451.imnbt.utils.nbt.UnlimitedReadTracker;
import net.lenni0451.imnbt.utils.nbt.diff.DiffType;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.close;

public class MainWindow extends Window {

    private static final String ERROR_REQUIRE_TAG = "You need to create or open a Nbt Tag first.";
    private static final String SUCCESS_SAVE = "Successfully saved the Nbt Tag.";
    private static final String ERROR_OPEN = "An unknown error occurred while opening the Nbt Tag.";
    private static final String ERROR_SAVE = "An unknown error occurred while saving the Nbt Tag.";
    private static final String REQUIRE_BOTH_DIFF_TAGS = "You need to select two Nbt Tags to compare them.";


    private final List<Tag> tags = new ArrayList<>();
    private int openTab;
    private INbtTag leftDiff;
    private INbtTag rightDiff;

    @Override
    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {
                    this.open();
                }
                if (ImGui.menuItem("Save")) {
                    this.save();
                }
                if (ImGui.menuItem("Close")) {
                    this.tags.remove(this.openTab);
                    this.openTab--;
                }
                if (ImGui.beginMenu("New Root Tag")) {
                    this.newRootTag();

                    ImGui.endMenu();
                }
                ImGui.separator();
                if (ImGui.menuItem("Exit")) {
                    System.exit(0);
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Font Size")) {
                ImFont[] fonts = Main.getInstance().getConfig().getFonts();
                int usedFont = Main.getInstance().getConfig().getUsedFont();
                for (int i = 0; i < fonts.length; i++) {
                    ImFont font = fonts[i];
                    if (ImGui.menuItem("Size " + String.format("%.0f", font.getFontSize()), "", i == usedFont)) {
                        Main.getInstance().getConfig().setUsedFont(i);
                    }
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("SNbt")) {
                if (ImGui.menuItem("SNbt Parser")) {
                    Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
                    Main.getInstance().getImGuiImpl().openPopup(new SNbtParserPopup((p, success) -> {
                        if (success) {
                            if (tag != null && tag.tag == null) tag.tag = p.getParsedTag();
                            else this.tags.add(new Tag(p.getParsedTag()));
                        }
                        Main.getInstance().getImGuiImpl().closePopup();
                    }));
                }
                if (ImGui.menuItem("SNbt Serializer")) {
                    if (!this.hasTag()) Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close()));
                    else Main.getInstance().getImGuiImpl().openPopup(new SNbtSerializerPopup(this.tags.get(this.openTab).tag, close()));
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Diff")) {
                if (ImGui.menuItem("Select Left", "", this.leftDiff != null)) {
                    if (this.hasTag()) this.leftDiff = this.tags.get(this.openTab).tag;
                    else Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close()));
                }
                if (ImGui.menuItem("Select Right", "", this.rightDiff != null)) {
                    if (this.hasTag()) this.rightDiff = this.tags.get(this.openTab).tag;
                    else Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close()));
                }
                if (ImGui.menuItem("Diff")) {
                    if (this.leftDiff == null || this.rightDiff == null) {
                        Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", REQUIRE_BOTH_DIFF_TAGS, close()));
                    } else {
                        Main.getInstance().getImGuiImpl().getDiffWindow().diff(this.leftDiff, this.rightDiff);
                        Main.getInstance().getImGuiImpl().getDiffWindow().show();
                    }
                }
                if (ImGui.beginMenu("Legend")) {
                    for (DiffType value : DiffType.values()) {
                        Color color = value.getColor();
                        if (color != null) ImGui.pushStyleColor(ImGuiCol.Text, color.getABGR());
                        ImGui.text(StringUtils.format(value));
                        if (color != null) ImGui.popStyleColor();
                    }

                    ImGui.endMenu();
                }

                ImGui.endMenu();
            }
            if (ImGui.menuItem("About")) {
                Main.getInstance().getImGuiImpl().getAboutWindow().show();
            }

            ImGui.endMenuBar();
        }

        if (this.tags.isEmpty()) {
            ImGui.text("No Nbt Tags loaded");
        } else {
            if (ImGui.beginTabBar("##Tags")) {
                for (int i = 0; i < this.tags.size(); i++) {
                    ImGui.pushID(i);
                    Tag tag = this.tags.get(i);
                    ImBoolean open = new ImBoolean(true);
                    if (ImGui.beginTabItem(tag.settings.rootName.isEmpty() ? "<empty>" : tag.settings.rootName, open)) {
                        this.openTab = i;
                        if (tag.tag == null) {
                            ImGui.text("No Nbt Tag present");
                        } else {
                            ImGui.beginChild("##NbtTree");
                            NbtTreeRenderer.render(newName -> tag.settings.rootName = newName, () -> tag.tag = null, p -> null, true, "", tag.settings.rootName, tag.tag);
                            ImGui.endChild();
                        }

                        ImGui.endTabItem();
                    }
                    if (!open.get()) {
                        this.tags.remove(i);
                        i--;
                    }
                    ImGui.popID();
                }

                ImGui.endTabBar();
            }
        }
    }

    private boolean hasTag() {
        return this.tags.size() > 0 && this.tags.get(this.openTab).tag != null;
    }

    private void open() {
        String response = FileDialogs.open("Open Nbt Tag");
        if (response == null) return;

        byte[] data;
        try (FileInputStream fis = new FileInputStream(response)) {
            data = fis.readAllBytes();
        } catch (Throwable t) {
            t.printStackTrace();
            Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_OPEN, close()));
            return;
        }
        this.open(data);
    }

    @Override
    public void dragAndDrop(File file, byte[] data) {
        this.open(data);
    }

    private void open(final byte[] data) {
        Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
        Main.getInstance().getImGuiImpl().openPopup(new OpenFilePopup(data, (p, success) -> {
            if (success) {
                try {
                    DataInput dataInput = p.getTagSettings().endianType.wrap(p.getTagSettings().compressionType.wrap(new ByteArrayInputStream(data)));
                    INbtTag nbtTag = p.getTagSettings().formatType.getNbtIO().read(dataInput, UnlimitedReadTracker.INSTANCE);
                    if (tag != null && tag.tag == null) {
                        tag.settings = p.getTagSettings();
                        tag.tag = nbtTag;
                    } else {
                        this.tags.add(new Tag(p.getTagSettings(), nbtTag));
                    }
                    Main.getInstance().getImGuiImpl().closePopup();
                } catch (Throwable t) {
                    t.printStackTrace();
                    Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_OPEN, close()));
                }
            } else {
                Main.getInstance().getImGuiImpl().closePopup();
            }
        }));
    }

    private void save() {
        if (!this.hasTag()) {
            Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close()));
            return;
        }

        String response = FileDialogs.save("Save Nbt Tag");
        if (response != null) {
            Tag tag = this.tags.get(this.openTab);
            Main.getInstance().getImGuiImpl().openPopup(new SaveFilePopup(tag.settings, (p, success) -> {
                if (success) {
                    try {
                        FileOutputStream fos = new FileOutputStream(response);
                        OutputStream os = tag.settings.compressionType.wrap(fos);
                        try (os) {
                            DataOutput dataOutput = tag.settings.endianType.wrap(os);
                            tag.settings.formatType.getNbtIO().write(dataOutput, tag.settings.rootName, tag.tag);
                            Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Success", SUCCESS_SAVE, close()));
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_SAVE, close()));
                    }
                } else {
                    Main.getInstance().getImGuiImpl().closePopup();
                }
            }));
        }
    }

    private void newRootTag() {
        Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
        for (NbtType value : NbtType.values()) {
            if (NbtType.END.equals(value)) continue;
            if (ImGui.menuItem(StringUtils.format(value))) {
                Main.getInstance().getImGuiImpl().openPopup(new EditTagPopup("New " + StringUtils.format(value) + " Tag", "Create", "", value.newInstance(), (p, success) -> {
                    if (success) {
                        if (tag != null && tag.tag == null) {
                            tag.settings.rootName = p.getName();
                            tag.tag = p.getTag();
                        } else {
                            Tag newTag = new Tag(p.getTag());
                            newTag.settings.rootName = p.getName();
                            this.tags.add(newTag);
                        }
                    }
                    Main.getInstance().getImGuiImpl().closePopup();
                }));
                break;
            }
        }
    }


    private static class Tag {
        private TagSettings settings;
        private INbtTag tag;

        private Tag(final INbtTag tag) {
            this(new TagSettings(), tag);
        }

        private Tag(final TagSettings settings, final INbtTag tag) {
            this.settings = settings;
            this.tag = tag;
        }
    }

}
