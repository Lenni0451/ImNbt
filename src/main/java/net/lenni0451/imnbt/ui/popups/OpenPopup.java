package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import imgui.type.ImInt;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.utils.FormatDetector;

public class OpenPopup extends Popup {

    private final TagSettings tagSettings;
    private final ImInt selectedFormat;
    private final ImInt selectedEndian;
    private final ImInt selectedCompression;

    public OpenPopup(final byte[] data, final TagSettings tagSettings, final PopupCallback callback) {
        super("Open Nbt Tag", callback);

        FormatDetector detector = new FormatDetector(data);
        tagSettings.compressionType = detector.getCompressionType();
        tagSettings.endianType = detector.getEndianType();
        tagSettings.formatType = detector.getFormatType();

        this.tagSettings = tagSettings;
        this.selectedFormat = new ImInt(this.tagSettings.formatType.ordinal());
        this.selectedEndian = new ImInt(this.tagSettings.endianType.ordinal());
        this.selectedCompression = new ImInt(this.tagSettings.compressionType.ordinal());
    }

    @Override
    protected void renderContent() {
        if (ImGui.combo("##Format", this.selectedFormat, FormatType.NAMES)) {
            this.tagSettings.formatType = FormatType.values()[this.selectedFormat.get()];
        }
        if (ImGui.combo("##Endian", this.selectedEndian, EndianType.NAMES)) {
            this.tagSettings.endianType = EndianType.values()[this.selectedEndian.get()];
        }
        if (ImGui.combo("##Compression", this.selectedCompression, CompressionType.NAMES)) {
            this.tagSettings.compressionType = CompressionType.values()[this.selectedCompression.get()];
        }

        ImGui.separator();
        if (ImGui.button("Open")) {
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
