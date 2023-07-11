package net.lenni0451.imnbt.ui.windows;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.lenni0451.imnbt.FontHandler;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.popups.file.OpenFilePopup;
import net.lenni0451.imnbt.ui.popups.file.SaveFilePopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtParserPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.nbt.TagUtils;
import net.lenni0451.imnbt.utils.nbt.UnlimitedReadTracker;
import net.lenni0451.imnbt.utils.nbt.diff.DiffType;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.io.NamedTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import javax.annotation.Nullable;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.close;

/**
 * The main window of ImNbt.
 */
public class MainWindow extends Window {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
    private static final String ERROR_REQUIRE_TAG = "You need to create or open a Nbt Tag first.";
    private static final String SUCCESS_SAVE = "Successfully saved the Nbt Tag.";
    private static final String ERROR_OPEN = "An unknown error occurred while opening the Nbt Tag.";
    private static final String ERROR_SAVE = "An unknown error occurred while saving the Nbt Tag.";
    private static final String REQUIRE_BOTH_DIFF_TAGS = "You need to select two Nbt Tags to compare them.";
    private static final String WARNING_UNREAD_BYTES = "After reading the Nbt Tag there are still %s unread bytes left.\nTry enabling 'Read extra data' if there are more tags in the file.";

    private final FontHandler fontHandler;
    private final List<Tag> tags = new ArrayList<>();
    private final SearchProvider searchProvider = new SearchProvider(this.drawer);
    private final ImString searchText = new ImString(1024);
    private boolean searchModified = false;
    private int openTab;
    private INbtTag leftDiff;
    private INbtTag rightDiff;

    /**
     * Create a new main window.<br>
     * The font handler is required when using the font size menu. If you don't want to use it you can pass null.
     *
     * @param drawer      The drawer instance
     * @param fontHandler The font handler to use
     */
    public MainWindow(final ImNbtDrawer drawer, @Nullable final FontHandler fontHandler) {
        super(drawer);
        this.fontHandler = fontHandler;
    }

    /**
     * @return The tag of the currently open tab
     */
    @Nullable
    public INbtTag getOpenTab() {
        if (this.tags.isEmpty()) return null;
        return this.tags.get(this.openTab).tag;
    }

    /**
     * @return The tags of all tabs
     */
    public INbtTag[] getTabs() {
        if (this.tags.isEmpty()) return new INbtTag[0];
        return this.tags.stream().map(tag -> tag.tag).toArray(INbtTag[]::new);
    }

    @Override
    public void render() {
        boolean searchDeactivated = false;
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {
                    this.chooseFile();
                }
                if (ImGui.menuItem("Save")) {
                    this.saveFile();
                }
                if (ImGui.menuItem("Close")) {
                    if (!this.tags.isEmpty()) {
                        this.tags.remove(this.openTab);
                        this.openTab--;
                    }
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
            if (this.fontHandler != null && ImGui.beginMenu("Font Size")) {
                ImFont[] fonts = this.fontHandler.getFonts();
                int usedFont = this.fontHandler.getSelectedFont();
                for (int i = 0; i < fonts.length; i++) {
                    ImFont font = fonts[i];
                    if (ImGui.menuItem("Size " + String.format("%.0f", font.getFontSize()), "", i == usedFont)) {
                        this.fontHandler.setSelectedFont(i);
                    }
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Search")) {
                if (ImGui.inputText("Search", this.searchText)) {
                    this.searchProvider.setSearch(this.searchText.get());
                    this.searchModified = true;
                }
                searchDeactivated = ImGui.isItemDeactivated();
                if (ImGui.button("Previous")) {
                    this.searchProvider.setDoScroll(SearchProvider.SearchDirection.PREVIOUS);
                }
                ImGui.sameLine();
                if (ImGui.button("Next")) {
                    this.searchProvider.setDoScroll(SearchProvider.SearchDirection.NEXT);
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("SNbt")) {
                if (ImGui.menuItem("SNbt Parser")) {
                    Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
                    this.drawer.openPopup(new SNbtParserPopup((p, success) -> {
                        if (success) {
                            if (tag != null && tag.tag == null) {
                                tag.settings.rootName = "";
                                tag.tag = p.getParsedTag();
                                tag.fileName = null;
                            } else {
                                this.tags.add(new Tag(p.getParsedTag()));
                            }
                        }
                        this.drawer.closePopup();
                    }));
                }
                if (ImGui.menuItem("SNbt Serializer")) {
                    if (!this.hasTag()) this.drawer.openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close(this.drawer)));
                    else this.drawer.openPopup(new SNbtSerializerPopup(this.tags.get(this.openTab).tag, close(this.drawer)));
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Diff")) {
                if (ImGui.menuItem("Select Left", "", this.leftDiff != null)) {
                    if (this.hasTag()) this.leftDiff = this.tags.get(this.openTab).tag;
                    else this.drawer.openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close(this.drawer)));
                }
                if (ImGui.menuItem("Select Right", "", this.rightDiff != null)) {
                    if (this.hasTag()) this.rightDiff = this.tags.get(this.openTab).tag;
                    else this.drawer.openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close(this.drawer)));
                }
                if (ImGui.menuItem("Diff")) {
                    if (this.leftDiff == null || this.rightDiff == null) {
                        this.drawer.openPopup(new MessagePopup("Error", REQUIRE_BOTH_DIFF_TAGS, close(this.drawer)));
                    } else {
                        this.drawer.getDiffWindow().diff(this.leftDiff, this.rightDiff);
                        this.drawer.getDiffWindow().show();
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
                this.drawer.getAboutWindow().show();
            }

            ImGui.endMenuBar();
        }
        if (this.searchModified && searchDeactivated) {
            this.searchModified = false;

            Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
            if (tag != null) this.searchProvider.buildSearchPaths(tag.tag);
        }

        if (this.tags.isEmpty()) {
            ImGui.text("No Nbt Tags loaded");
        } else {
            if (ImGui.beginTabBar("##Tags")) {
                for (int i = 0; i < this.tags.size(); i++) {
                    ImGui.pushID(i);
                    Tag tag = this.tags.get(i);
                    ImBoolean open = new ImBoolean(true);
                    String tabName = tag.settings.rootName;
                    if (tabName.isEmpty()) tabName = tag.fileName;
                    if (tabName == null) tabName = "<empty>";
                    if (ImGui.beginTabItem(tabName, open)) {
                        this.openTab = i;
                        if (tag.tag == null) {
                            ImGui.text("No Nbt Tag present");
                        } else {
                            ImGui.beginChild("##NbtTree");
                            NbtTreeRenderer.render(drawer, newName -> tag.settings.rootName = newName, () -> {
                                tag.settings.rootName = "";
                                tag.tag = null;
                                tag.fileName = null;
                                this.searchProvider.buildSearchPaths(null);
                            }, p -> null, this.searchProvider, true, "", tag.settings.rootName, tag.tag);
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

    public void chooseFile() {
        String response = this.drawer.showOpenFileDialog("Open Nbt Tag");
        if (response == null) return;
        File file = new File(response);

        byte[] data;
        try (FileInputStream fis = new FileInputStream(file)) {
            data = fis.readAllBytes();
        } catch (Throwable t) {
            t.printStackTrace();
            this.drawer.openPopup(new MessagePopup("Error", ERROR_OPEN, close(this.drawer)));
            return;
        }
        this.open(data, file.getName());
    }

    @Override
    public void dragAndDrop(File file, byte[] data) {
        this.open(data, file.getName());
    }

    public void open(final byte[] data, final String fileName) {
        Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
        this.drawer.openPopup(new OpenFilePopup(data, (p, success) -> {
            if (success) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    DataInput dataInput = p.getTagSettings().endianType.wrap(p.getTagSettings().compressionType.wrap(bais));
                    NamedTag namedTag = p.getTagSettings().formatType.getNbtIO().readNamed(dataInput, UnlimitedReadTracker.INSTANCE);
                    if (bais.available() > 0 && p.getTagSettings().readExtraData) {
                        CompoundTag extraCompound = new CompoundTag(new LinkedHashMap<>());
                        NamedTag extraRoot = new NamedTag("extra data", NbtType.COMPOUND, extraCompound);
                        if (namedTag != null) extraCompound.add(namedTag.getName(), namedTag.getTag());
                        namedTag = extraRoot;

                        while (bais.available() > 0) {
                            NamedTag extra = p.getTagSettings().formatType.getNbtIO().readNamed(dataInput, UnlimitedReadTracker.INSTANCE);
                            if (extra == null) continue;
                            extraCompound.add(TagUtils.findUniqueName(extraCompound, extra.getName()), extra.getTag());
                        }
                    }
                    if (tag != null && tag.tag == null) {
                        tag.settings = p.getTagSettings();
                        if (namedTag == null) {
                            tag.tag = null;
                        } else {
                            tag.settings.rootName = namedTag.getName();
                            tag.tag = namedTag.getTag();
                        }
                        tag.fileName = fileName;
                    } else {
                        Tag readTag;
                        if (namedTag == null) {
                            readTag = new Tag(p.getTagSettings(), null);
                        } else {
                            readTag = new Tag(p.getTagSettings(), namedTag.getTag());
                            readTag.settings.rootName = namedTag.getName();
                        }
                        readTag.fileName = fileName;
                        this.tags.add(readTag);
                    }
                    if (bais.available() > 0) {
                        this.drawer.openPopup(new MessagePopup("Warning", String.format(WARNING_UNREAD_BYTES, DECIMAL_FORMAT.format(bais.available())), close(this.drawer)));
                    } else {
                        this.drawer.closePopup();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    this.drawer.openPopup(new MessagePopup("Error", ERROR_OPEN, close(this.drawer)));
                }
            } else {
                this.drawer.closePopup();
            }
        }));
    }

    public void saveFile() {
        if (!this.hasTag()) {
            this.drawer.openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, close(this.drawer)));
            return;
        }

        String response = this.drawer.showSaveFileDialog("Save Nbt Tag");
        if (response != null) {
            Tag tag = this.tags.get(this.openTab);
            this.drawer.openPopup(new SaveFilePopup(tag.settings, (p, success) -> {
                if (success) {
                    try {
                        FileOutputStream fos = new FileOutputStream(response);
                        OutputStream os = tag.settings.compressionType.wrap(fos);
                        try (os) {
                            DataOutput dataOutput = tag.settings.endianType.wrap(os);
                            tag.settings.formatType.getNbtIO().write(dataOutput, tag.settings.rootName, tag.tag);
                            this.drawer.openPopup(new MessagePopup("Success", SUCCESS_SAVE, close(this.drawer)));
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        this.drawer.openPopup(new MessagePopup("Error", ERROR_SAVE, close(this.drawer)));
                    }
                } else {
                    this.drawer.closePopup();
                }
            }));
        }
    }

    private void newRootTag() {
        Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
        for (NbtType value : NbtType.values()) {
            if (NbtType.END.equals(value)) continue;
            if (ImGui.menuItem("     " + StringUtils.format(value))) {
                this.drawer.openPopup(new EditTagPopup("New " + StringUtils.format(value) + " Tag", "Create", "", value.newInstance(), (p, success) -> {
                    if (success) {
                        if (tag != null && tag.tag == null) {
                            tag.settings.rootName = p.getName();
                            tag.tag = p.getTag();
                            tag.fileName = null;
                        } else {
                            Tag newTag = new Tag(p.getTag());
                            newTag.settings.rootName = p.getName();
                            this.tags.add(newTag);
                        }
                    }
                    this.drawer.closePopup();
                }));
                break;
            }
            { //Render icon
                ImVec2 xy = ImGui.getItemRectMin();
                xy.x++;
                xy.y += 2;
                NbtTreeRenderer.renderIcon(this.drawer, xy, value);
            }
        }
    }


    private static class Tag {
        private TagSettings settings;
        @Nullable
        private INbtTag tag;
        private String fileName;

        private Tag(final INbtTag tag) {
            this(new TagSettings(), tag);
        }

        private Tag(final TagSettings settings, @Nullable final INbtTag tag) {
            this.settings = settings;
            this.tag = tag;
        }
    }

}
