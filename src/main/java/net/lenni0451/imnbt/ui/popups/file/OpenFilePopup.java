package net.lenni0451.imnbt.ui.popups.file;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.utils.nbt.FormatDetector;

/**
 * A popup to choose the settings when opening a tag.
 */
public class OpenFilePopup extends Popup<OpenFilePopup> {

    private final TagSettings tagSettings;
    private final ImInt selectedFormat;
    private final ImInt selectedEndian;
    private final ImInt selectedCompression;
    private final ImBoolean namelessRoot;
    private final ImBoolean readExtraData;
    private final ImBoolean bedrockLevelDat;

    public OpenFilePopup(final byte[] data, final PopupCallback<OpenFilePopup> callback) {
        super("Open Nbt Tag", callback);

        FormatDetector detector = new FormatDetector(data);
        this.tagSettings = new TagSettings();
        this.tagSettings.compressionType = detector.getCompressionType();
        this.tagSettings.endianType = detector.getEndianType();
        this.tagSettings.formatType = detector.getFormatType();
        this.tagSettings.bedrockLevelDat = detector.isBedrockLevelDat();

        this.selectedFormat = new ImInt(this.tagSettings.formatType.ordinal());
        this.selectedEndian = new ImInt(this.tagSettings.endianType.ordinal());
        this.selectedCompression = new ImInt(this.tagSettings.compressionType.ordinal());
        this.namelessRoot = new ImBoolean(this.tagSettings.namelessRoot);
        this.readExtraData = new ImBoolean(this.tagSettings.readExtraData);
        this.bedrockLevelDat = new ImBoolean(this.tagSettings.bedrockLevelDat);
    }

    public TagSettings getTagSettings() {
        return this.tagSettings;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
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
        if (ImGui.checkbox("Read Extra Data", this.readExtraData)) {
            this.tagSettings.readExtraData = this.readExtraData.get();
        }
        if (ImGui.checkbox("Bedrock Level .dat", this.bedrockLevelDat)) {
            this.tagSettings.bedrockLevelDat = this.bedrockLevelDat.get();
        }

        ImGui.separator();
        if (ImGui.button("Open")) {
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
