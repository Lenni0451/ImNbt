package net.lenni0451.imnbt.ui.popups.file;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.config.ImNbtConfig;
import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.CustomFormatType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.utils.nbt.detector.BasicDetector;
import net.lenni0451.imnbt.utils.nbt.detector.BruteForceDetector;

/**
 * A popup to choose the settings when opening a tag.
 */
public class OpenFilePopup extends Popup<OpenFilePopup> {

    private final TagSettings tagSettings;
    private final ImInt selectedFormat;
    private final ImInt selectedEndian;
    private final ImInt selectedCompression;
    private final ImInt customFormatType;
    private final ImBoolean namelessRoot;
    private final ImBoolean readExtraData;
    private int unreadBytes;
    private Thread advancedDetectorThread;
    private boolean startedAdvancedDetection;

    public OpenFilePopup(final byte[] data, final PopupCallback<OpenFilePopup> callback) {
        super("Open Nbt Tag", callback);

        this.tagSettings = new TagSettings();
        this.selectedFormat = new ImInt(this.tagSettings.formatType.ordinal());
        this.selectedEndian = new ImInt(this.tagSettings.endianType.ordinal());
        this.selectedCompression = new ImInt(this.tagSettings.compressionType.ordinal());
        this.customFormatType = new ImInt(this.tagSettings.customFormatType.ordinal());
        this.namelessRoot = new ImBoolean(this.tagSettings.namelessRoot);
        this.readExtraData = new ImBoolean(this.tagSettings.readExtraData);
        this.runBasicDetector(data);
        this.initAdvancedDetector(data);
    }

    public TagSettings getTagSettings() {
        return this.tagSettings;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        if (!this.startedAdvancedDetection && drawer.getConfig().getBoolean(ImNbtConfig.ADVANCED_FORMAT_DETECTION, false)) {
            //The advanced detection is only started if the user has enabled it in the config because it can take a long time
            //It has to be started here for config access in the drawer
            this.startedAdvancedDetection = true;
            this.advancedDetectorThread.start();
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
        if (ImGui.combo("##CustomFormat", this.customFormatType, CustomFormatType.NAMES)) {
            this.tagSettings.customFormatType = CustomFormatType.values()[this.customFormatType.get()];
        }
        if (ImGui.checkbox("Nameless root tag", this.namelessRoot)) {
            this.tagSettings.namelessRoot = this.namelessRoot.get();
        }
        if (ImGui.checkbox("Read Extra Data" + (this.unreadBytes > 0 ? (" (" + this.unreadBytes + " extra)") : ""), this.readExtraData)) {
            this.tagSettings.readExtraData = this.readExtraData.get();
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
        if (this.advancedDetectorThread.isAlive()) {
            ImGui.sameLine();
            ImGui.text("Detecting...");
        }
    }

    private void runBasicDetector(final byte[] data) {
        BasicDetector basicDetector = new BasicDetector(data);
        this.tagSettings.compressionType = basicDetector.getCompressionType();
        this.tagSettings.endianType = basicDetector.getEndianType();
        this.tagSettings.formatType = basicDetector.getFormatType();
        this.tagSettings.customFormatType = basicDetector.getCustomFormatType();
        this.copySettingsToUI();
    }

    private void initAdvancedDetector(final byte[] data) {
        this.advancedDetectorThread = new Thread(() -> {
            BruteForceDetector bruteForceDetector = new BruteForceDetector(data);
            bruteForceDetector.run().ifPresent(result -> {
                this.tagSettings.compressionType = result.compressionType();
                this.tagSettings.endianType = result.endianType();
                this.tagSettings.formatType = result.formatType();
                this.tagSettings.customFormatType = result.customFormatType();
                this.tagSettings.namelessRoot = result.namelessRoot();
                this.unreadBytes = result.readResult().unreadBytes();
                this.copySettingsToUI();
            });
        }, "Advanced Format Detector");
        this.advancedDetectorThread.setUncaughtExceptionHandler((t, e) -> {}); //Just pretend the current format is the best one
        this.advancedDetectorThread.setDaemon(true);
    }

    private void copySettingsToUI() {
        this.selectedFormat.set(this.tagSettings.formatType.ordinal());
        this.selectedEndian.set(this.tagSettings.endianType.ordinal());
        this.selectedCompression.set(this.tagSettings.compressionType.ordinal());
        this.customFormatType.set(this.tagSettings.customFormatType.ordinal());
        this.namelessRoot.set(this.tagSettings.namelessRoot);
        this.readExtraData.set(this.tagSettings.readExtraData);
    }

}
