package net.lenni0451.imnbt.ui.windows;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiTabItemFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.lenni0451.imnbt.FontHandler;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.config.ImNbtConfig;
import net.lenni0451.imnbt.types.formats.ICustomFormat;
import net.lenni0451.imnbt.types.formats.NoneFormat;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.IntegerInputPopup;
import net.lenni0451.imnbt.ui.popups.file.OpenFilePopup;
import net.lenni0451.imnbt.ui.popups.file.SaveFilePopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtParserPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.NotificationLevel;
import net.lenni0451.imnbt.utils.NumberUtils;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.nbt.NbtReader;
import net.lenni0451.imnbt.utils.nbt.ReadTrackers;
import net.lenni0451.imnbt.utils.nbt.TagSorter;
import net.lenni0451.imnbt.utils.nbt.TagVisitor;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.DoubleTag;
import net.lenni0451.mcstructs.nbt.tags.FloatTag;

import javax.annotation.Nullable;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private static final String WARNING_UNREAD_BYTES = "After reading the Nbt Tag there are still %s unread bytes left.\nTry enabling 'Read extra data' if there are more tags in the file.";

    private final FontHandler fontHandler;
    private final List<Tag> tags = new ArrayList<>();
    private final SearchProvider searchProvider = new SearchProvider(this.drawer);
    private final ImString searchText = new ImString(1024);
    private boolean searchModified = false;
    private boolean highlightSearch = false;
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

    /**
     * Highlight the search box.
     */
    public void highlightSearch() {
        this.highlightSearch = true;
    }

    /**
     * Undo the last action.<br>
     * If no action can be undone nothing will happen.
     */
    public void undo() {
        boolean hasTag = this.openTab >= 0 && this.openTab < this.tags.size();
        if (!hasTag) return;
        List<Object> history = this.tags.get(this.openTab).history;
        List<Object> undoHistory = this.tags.get(this.openTab).undoHistory;
        if (history.isEmpty()) return;

        Tag tag = this.tags.get(this.openTab);
        Object last = history.remove(history.size() - 1);
        if (last instanceof INbtTag oldTag) {
            undoHistory.add(tag.tag.copy());
            tag.tag = oldTag;
            this.searchProvider.buildSearchPaths(tag.tag);
        } else if (last instanceof String oldRootName) {
            undoHistory.add(tag.settings.rootName);
            tag.settings.rootName = oldRootName;
        } else if (last instanceof TagHistory oldTagHistory) {
            undoHistory.add(new TagHistory(tag.settings.rootName, Optional.ofNullable(tag.tag).map(INbtTag::copy).orElse(null), tag.fileName, tag.filePath));
            tag.settings.rootName = oldTagHistory.rootName;
            tag.tag = oldTagHistory.tag;
            tag.fileName = oldTagHistory.fileName;
            tag.filePath = oldTagHistory.filePath;
            this.searchProvider.buildSearchPaths(tag.tag);
        } else {
            this.drawer.showNotification(NotificationLevel.ERROR, "Error", "Unknown history type: " + last.getClass().getName() + "\nThe history has been cleared to prevent further errors.", () -> {
                history.clear();
                undoHistory.clear();
            });
        }
        tag.modified = true;
    }

    /**
     * Redo the last undone action.<br>
     * If no action can be redone nothing will happen.
     */
    public void redo() {
        boolean hasTag = this.openTab >= 0 && this.openTab < this.tags.size();
        if (!hasTag) return;
        List<Object> history = this.tags.get(this.openTab).history;
        List<Object> undoHistory = this.tags.get(this.openTab).undoHistory;
        if (undoHistory.isEmpty()) return;

        Tag tag = this.tags.get(this.openTab);
        Object last = undoHistory.remove(undoHistory.size() - 1);
        if (last instanceof INbtTag newTag) {
            history.add(tag.tag.copy());
            tag.tag = newTag;
            this.searchProvider.buildSearchPaths(tag.tag);
        } else if (last instanceof String newRootName) {
            history.add(tag.settings.rootName);
            tag.settings.rootName = newRootName;
        } else if (last instanceof TagHistory newTagHistory) {
            history.add(new TagHistory(tag.settings.rootName, Optional.ofNullable(tag.tag).map(INbtTag::copy).orElse(null), tag.fileName, tag.filePath));
            tag.settings.rootName = newTagHistory.rootName;
            tag.tag = newTagHistory.tag;
            tag.fileName = newTagHistory.fileName;
            tag.filePath = newTagHistory.filePath;
            this.searchProvider.buildSearchPaths(tag.tag);
        } else {
            this.drawer.showNotification(NotificationLevel.ERROR, "Error", "Unknown history type: " + last.getClass().getName() + "\nThe history has been cleared to prevent further errors.", () -> {
                history.clear();
                undoHistory.clear();
            });
        }
        tag.modified = true;
    }

    /**
     * Save the currently open tab.<br>
     * If the file has a path associated with it, it will be saved to that path.<br>
     * If the file has no path associated with it, a save dialog will be opened.<br>
     * If no tab is open or the current tag is not modified nothing will happen.
     */
    public void saveCurrent() {
        if (!this.hasTag()) return;
        Tag tag = this.tags.get(this.openTab);
        if (!tag.modified) return;
        if (tag.filePath == null) {
            this.saveFile();
        } else {
            this.writeFile(tag, tag.filePath);
        }
    }

    @Override
    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {
                    this.chooseFile();
                }
                if (ImGui.menuItem("Save", null, false, this.hasTag() && this.tags.get(this.openTab).modified && this.tags.get(this.openTab).filePath != null)) {
                    Tag tag = this.tags.get(this.openTab);
                    this.writeFile(tag, tag.filePath);
                }
                if (ImGui.menuItem("Save As", null, false, this.hasTag())) {
                    this.saveFile();
                }
                if (ImGui.menuItem("Close", null, false, !this.tags.isEmpty())) {
                    this.tags.remove(this.openTab);
                    this.openTab--;
                }
                if (ImGui.beginMenu("New Root Tag")) {
                    this.newRootTag();

                    ImGui.endMenu();
                }
                ImGui.separator();
                if (ImGui.menuItem("Exit")) {
                    this.drawer.exit();
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Edit")) {
                boolean hasTag = this.openTab >= 0 && this.openTab < this.tags.size();
                List<Object> history = hasTag ? this.tags.get(this.openTab).history : null;
                List<Object> undoHistory = hasTag ? this.tags.get(this.openTab).undoHistory : null;
                if (ImGui.menuItem("Undo", hasTag ? String.valueOf(history.size()) : null, false, hasTag && !history.isEmpty())) {
                    this.undo();
                }
                if (ImGui.menuItem("Redo", hasTag ? String.valueOf(undoHistory.size()) : null, false, hasTag && !undoHistory.isEmpty())) {
                    this.redo();
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Batch")) {
                if (ImGui.menuItem("Round Numbers", null, false, this.hasTag())) {
                    this.drawer.openPopup(new IntegerInputPopup("Round", "Enter the amount of decimals to round to", 0, 10, 2, decimalPlaces -> {
                        Tag rootTag = this.tags.get(this.openTab);
                        TagVisitor.visit(rootTag.tag, tag -> {
                            if (tag instanceof FloatTag floatTag) {
                                rootTag.history.add(rootTag.tag.copy());
                                rootTag.undoHistory.clear();
                                floatTag.setValue(NumberUtils.round(floatTag.getValue(), decimalPlaces));
                                rootTag.modified = true;
                            } else if (tag instanceof DoubleTag doubleTag) {
                                rootTag.history.add(rootTag.tag.copy());
                                rootTag.undoHistory.clear();
                                doubleTag.setValue(NumberUtils.round(doubleTag.getValue(), decimalPlaces));
                                rootTag.modified = true;
                            }
                        });
                    }, close(this.drawer)));
                }
                if (ImGui.menuItem("Sort List/Compound Tags", null, false, this.hasTag())) {
                    Tag rootTag = this.tags.get(this.openTab);
                    rootTag.history.add(rootTag.tag.copy());
                    rootTag.undoHistory.clear();
                    rootTag.modified = true;
                    TagVisitor.visit(rootTag.tag, TagSorter::sort);
                }

                ImGui.endMenu();
            }
            if (this.highlightSearch && !ImGui.isPopupOpen("Search")) ImGui.openPopup("Search");
            if (ImGui.beginMenu("Search")) {
                if (this.highlightSearch) {
                    this.highlightSearch = false;
                    ImGui.setKeyboardFocusHere();
                }
                ImGui.inputText("Search", this.searchText);
                if (ImGui.isKeyPressed(ImGuiKey.Enter)) {
                    this.highlightSearch = true;
                }
                if (ImGui.isItemDeactivated() && !this.searchProvider.getSearch().equals(this.searchText.get())) {
                    this.searchProvider.setSearch(this.searchText.get());
                    this.searchModified = true;
                }
                if (ImGui.button("Previous")) {
                    this.searchProvider.setDoScroll(SearchProvider.SearchDirection.PREVIOUS);
                }
                ImGui.sameLine();
                if (ImGui.button("Next") || ImGui.isKeyPressed(ImGuiKey.Enter)) {
                    this.searchProvider.setDoScroll(SearchProvider.SearchDirection.NEXT);
                }
                ImGui.sameLine();
                {
                    String s = "Results: ";
                    if (this.searchProvider.getCurrentScrollIndex() >= 0) {
                        s += this.searchProvider.getCurrentScrollIndex() + 1;
                        s += "/";
                    }
                    s += this.searchProvider.getPathCount();
                    ImGui.textUnformatted(s);
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("SNbt")) {
                if (ImGui.menuItem("SNbt Parser")) {
                    Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
                    this.drawer.openPopup(new SNbtParserPopup("", (p, success) -> {
                        if (success) {
                            if (tag != null && tag.tag == null) {
                                tag.settings.rootName = "";
                                tag.tag = p.getParsedTag();
                                tag.fileName = null;
                                tag.filePath = null;
                                tag.modified = true;
                            } else {
                                this.tags.add(new Tag(p.getParsedTag()));
                            }
                        }
                        this.drawer.closePopup();
                    }));
                }
                if (ImGui.menuItem("SNbt Serializer", null, false, this.hasTag())) {
                    this.drawer.openPopup(new SNbtSerializerPopup(this.tags.get(this.openTab).tag, close(this.drawer)));
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Diff")) {
                if (ImGui.menuItem("Select Left", "", this.leftDiff != null, this.hasTag())) {
                    if (this.hasTag()) this.leftDiff = this.tags.get(this.openTab).tag;
                    else this.drawer.showNotification(NotificationLevel.ERROR, "Error", ERROR_REQUIRE_TAG);
                }
                if (ImGui.menuItem("Select Right", "", this.rightDiff != null, this.hasTag())) {
                    if (this.hasTag()) this.rightDiff = this.tags.get(this.openTab).tag;
                    else this.drawer.showNotification(NotificationLevel.ERROR, "Error", ERROR_REQUIRE_TAG);
                }
                if (ImGui.menuItem("Diff", null, false, this.leftDiff != null && this.rightDiff != null)) {
                    this.drawer.getDiffWindow().diff(this.leftDiff, this.rightDiff);
                    this.drawer.getDiffWindow().show();
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Settings")) {
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
                if (ImGui.menuItem("Advanced Format Detector (slow)", this.drawer.getConfig().getBoolean(ImNbtConfig.ADVANCED_FORMAT_DETECTION, false))) {
                    this.drawer.getConfig().toggle(ImNbtConfig.ADVANCED_FORMAT_DETECTION, false);
                }

                ImGui.endMenu();
            }
            if (ImGui.menuItem("About")) {
                this.drawer.getAboutWindow().show();
            }

            ImGui.endMenuBar();
        }
        if (this.searchModified) {
            this.searchModified = false;

            Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
            if (tag != null) this.searchProvider.buildSearchPaths(tag.tag);
        }

        if (this.tags.isEmpty()) {
            ImGui.textUnformatted("No Nbt Tags loaded");
        } else {
            if (ImGui.beginTabBar("##Tags")) {
                for (int i = 0; i < this.tags.size(); i++) {
                    ImGui.pushID(i);
                    Tag tag = this.tags.get(i);
                    ImBoolean open = new ImBoolean(true);
                    String tabName = tag.settings.rootName;
                    int tabFlags = ImGuiTabItemFlags.None;
                    if (tabName.isEmpty()) tabName = tag.fileName;
                    if (tabName == null) tabName = "<empty>";
                    if (tag.tag != null) {
                        if (this.rightDiff == tag.tag) tabName = "(R) " + tabName;
                        if (this.leftDiff == tag.tag) tabName = "(L) " + tabName;
                    }
                    if (tag.modified) tabFlags |= ImGuiTabItemFlags.UnsavedDocument;
                    if (ImGui.beginTabItem(tabName + "###" + tag.uuid, open, tabFlags)) {
                        this.openTab = i;
                        if (tag.tag == null) {
                            ImGui.textUnformatted("No Nbt Tag present");
                        } else {
                            ImGui.beginChild("##NbtTree");
                            NbtTreeRenderer.render(drawer, newName -> {
                                tag.history.remove(tag.history.size() - 1); //Added by the modification listener
                                tag.history.add(tag.settings.rootName);

                                tag.settings.rootName = newName;
                                tag.modified = true;
                            }, (tranformedName, transformedTag) -> {
                                tag.history.remove(tag.history.size() - 1); //Added by the modification listener
                                tag.history.add(tag.tag.copy());

                                tag.tag = transformedTag;
                                this.searchProvider.buildSearchPaths(transformedTag);
                                tag.modified = true;
                            }, () -> {
                                tag.history.remove(tag.history.size() - 1); //Added by the modification listener
                                tag.history.add(new TagHistory(tag.settings.rootName, tag.tag.copy(), tag.fileName, tag.filePath));
                                tag.settings.rootName = "";

                                tag.tag = null;
                                tag.fileName = null;
                                tag.filePath = null;
                                this.searchProvider.buildSearchPaths(null);
                                tag.modified = true;
                            }, () -> {
                                tag.history.add(tag.tag.copy());
                                tag.undoHistory.clear();
                                tag.modified = true;
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
        return !this.tags.isEmpty() && this.tags.get(this.openTab).tag != null;
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
            this.drawer.showNotification(NotificationLevel.ERROR, "Error", ERROR_OPEN);
            return;
        }
        this.open(data, file.getName(), file.getAbsolutePath());
    }

    @Override
    public void dragAndDrop(File file, byte[] data) {
        this.open(data, file.getName(), file.getAbsolutePath());
    }

    public void open(final byte[] data, final String fileName, final String filePath) {
        Tag tag = this.tags.isEmpty() ? null : this.tags.get(this.openTab);
        this.drawer.openPopup(new OpenFilePopup(fileName, data, (p, success) -> {
            if (success) {
                try {
                    NbtReader.ReadResult readResult = NbtReader.read(data, () -> ReadTrackers.UNLIMITED, p.getTagSettings());
                    if (tag != null && tag.tag == null) {
                        tag.settings = p.getTagSettings();
                        tag.customFormat = readResult.customFormat();
                        readResult.namedTag().ifPresentOrElse(namedTag -> {
                            tag.settings.rootName = namedTag.getName();
                            tag.tag = namedTag.getTag();
                        }, () -> tag.tag = null);
                        tag.fileName = fileName;
                        tag.filePath = filePath;
                        tag.modified = false;
                        tag.history.clear();
                        tag.undoHistory.clear();
                    } else {
                        Tag readTag = readResult.namedTag().map(namedTag -> {
                            Tag newTag = new Tag(p.getTagSettings(), readResult.customFormat(), namedTag.getTag());
                            newTag.settings.rootName = namedTag.getName();
                            return newTag;
                        }).orElseGet(() -> new Tag(p.getTagSettings(), readResult.customFormat(), null));
                        readTag.fileName = fileName;
                        readTag.filePath = filePath;
                        readTag.modified = false;
                        this.tags.add(readTag);
                    }
                    if (readResult.unreadBytes() > 0) {
                        this.drawer.showNotification(NotificationLevel.WARNING, "Warning", String.format(WARNING_UNREAD_BYTES, DECIMAL_FORMAT.format(readResult.unreadBytes())), this.drawer::closePopup);
                    } else {
                        this.drawer.closePopup();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    this.drawer.showNotification(NotificationLevel.ERROR, "Error", ERROR_OPEN, this.drawer::closePopup);
                }
            } else {
                this.drawer.closePopup();
            }
        }));
    }

    public void saveFile() {
        String response = this.drawer.showSaveFileDialog("Save Nbt Tag");
        if (response != null) {
            Tag tag = this.tags.get(this.openTab);
            this.drawer.openPopup(new SaveFilePopup(tag.settings, (p, success) -> {
                if (success) {
                    this.writeFile(tag, response);
                } else {
                    this.drawer.closePopup();
                }
            }));
        }
    }

    private void writeFile(final Tag tag, final String file) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutput baosOut = tag.settings.endianType.wrap(baos);
            if (tag.settings.namelessRoot) {
                tag.settings.formatType.getNbtIO().writeUnnamed(baosOut, tag.tag);
            } else {
                tag.settings.formatType.getNbtIO().write(baosOut, tag.settings.rootName, tag.tag);
            }

            FileOutputStream fos = new FileOutputStream(file);
            OutputStream os = tag.settings.compressionType.wrap(fos);
            try (os) {
                DataOutput dataOutput = tag.settings.endianType.wrap(os);
                tag.customFormat.write(dataOutput, baos.toByteArray());
                tag.fileName = file.substring(file.lastIndexOf(File.separatorChar) + 1);
                tag.filePath = file;
                tag.modified = false;
                this.drawer.showNotification(NotificationLevel.INFO, "Success", SUCCESS_SAVE, this.drawer::closePopup);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            this.drawer.showNotification(NotificationLevel.ERROR, "Error", ERROR_SAVE, this.drawer::closePopup);
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
                            tag.filePath = null;
                            tag.modified = true;
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
        private final String uuid = UUID.randomUUID().toString();
        private TagSettings settings;
        private ICustomFormat customFormat;
        @Nullable
        private INbtTag tag;
        private String fileName;
        private String filePath;
        private boolean modified;
        private final List<Object> history;
        private final List<Object> undoHistory;

        private Tag(final INbtTag tag) {
            this(new TagSettings(), new NoneFormat(), tag);
        }

        private Tag(final TagSettings settings, final ICustomFormat customFormat, @Nullable final INbtTag tag) {
            this.settings = settings;
            this.customFormat = customFormat;
            this.tag = tag;
            this.modified = true;
            this.history = new ArrayList<>();
            this.undoHistory = new ArrayList<>();
        }
    }

    private record TagHistory(String rootName, INbtTag tag, String fileName, String filePath) {
    }

}
