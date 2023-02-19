package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.ui.types.Popup;

public class NewPopup extends Popup {

    private final TagSettings tagSettings;
    private final ImString rootName;
    private final ImInt selectedFormat;
    private final ImInt selectedEndian;
    private final ImInt selectedCompression;

    public NewPopup(final TagSettings tagSettings, final PopupCallback callback) {
        super("Create new tag", callback);

        this.tagSettings = tagSettings;
        this.rootName = new ImString(this.tagSettings.rootName, 256);
        this.selectedFormat = new ImInt(this.tagSettings.formatType.ordinal());
        this.selectedEndian = new ImInt(this.tagSettings.endianType.ordinal());
        this.selectedCompression = new ImInt(this.tagSettings.compressionType.ordinal());
    }

    @Override
    protected void renderContent() {
        if (ImGui.inputText("Root name", this.rootName)) {
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
        if (ImGui.button("Create")) {
            this.getCallback().onClose(true);
            this.close();
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            this.getCallback().onClose(false);
            this.close();
        }
    }

}
