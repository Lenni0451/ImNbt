package net.lenni0451.imnbt.ui.popups.file;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.ui.types.Popup;

/**
 * A popup to choose the settings when saving a tag.
 */
public class SaveFilePopup extends Popup<SaveFilePopup> {

    private final TagSettings tagSettings;
    private final ImString rootName;
    private final ImInt selectedFormat;
    private final ImInt selectedEndian;
    private final ImInt selectedCompression;
    private final ImBoolean namelessRoot;

    public SaveFilePopup(final TagSettings tagSettings, final PopupCallback<SaveFilePopup> callback) {
        super("Save Nbt Tag", callback);

        this.tagSettings = tagSettings;
        this.rootName = new ImString(this.tagSettings.rootName, 256);
        this.selectedFormat = new ImInt(this.tagSettings.formatType.ordinal());
        this.selectedEndian = new ImInt(this.tagSettings.endianType.ordinal());
        this.selectedCompression = new ImInt(this.tagSettings.compressionType.ordinal());
        this.namelessRoot = new ImBoolean(this.tagSettings.namelessRoot);
    }

    public TagSettings getTagSettings() {
        return this.tagSettings;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        if (!this.namelessRoot.get() && ImGui.inputText("Root name", this.rootName)) {
            this.tagSettings.rootName = this.rootName.get();
        }
        if (ImGui.combo("##Format", this.selectedFormat, FormatType.NAMES)) {
            this.tagSettings.formatType = FormatType.values()[this.selectedFormat.get()];
        }
        if (ImGui.combo("##Endian", this.selectedEndian, EndianType.NAMES)) {
            this.tagSettings.endianType = EndianType.values()[this.selectedEndian.get()];
        }
        if (ImGui.combo("##Compression", this.selectedCompression, CompressionType.NAMES)) {
            this.tagSettings.compressionType = CompressionType.values()[this.selectedCompression.get()];
        }
        if (ImGui.checkbox("Nameless root tag", this.namelessRoot)) {
            this.tagSettings.namelessRoot = this.namelessRoot.get();
        }

        ImGui.separator();
        if (ImGui.button("Save")) {
            this.getCallback().onClose(this, true);
            this.close();
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            this.getCallback().onClose(this, false);
            this.close();
        }
    }

}
