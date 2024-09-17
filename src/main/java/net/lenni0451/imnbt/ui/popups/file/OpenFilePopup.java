package net.lenni0451.imnbt.ui.popups.file;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.flag.ImGuiItemFlags;
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

import java.util.Optional;

/**
 * A popup to choose the settings when opening a tag.
 */
public class OpenFilePopup extends Popup<OpenFilePopup> {

    private final byte[] data;
    private final TagSettings tagSettings;
    private final ImInt selectedFormat;
    private final ImInt selectedEndian;
    private final ImInt selectedCompression;
    private final ImInt customFormatType;
    private final ImBoolean namelessRoot;
    private final ImBoolean readExtraData;
    private int unreadBytes;
    private Thread detectorThread;

    public OpenFilePopup(final byte[] data, final PopupCallback<OpenFilePopup> callback) {
        super("Open Nbt Tag", callback);

        this.data = data;
        this.tagSettings = new TagSettings();
        this.selectedFormat = new ImInt(this.tagSettings.formatType.ordinal());
        this.selectedEndian = new ImInt(this.tagSettings.endianType.ordinal());
        this.selectedCompression = new ImInt(this.tagSettings.compressionType.ordinal());
        this.customFormatType = new ImInt(this.tagSettings.customFormatType.ordinal());
        this.namelessRoot = new ImBoolean(this.tagSettings.namelessRoot);
        this.readExtraData = new ImBoolean(this.tagSettings.readExtraData);
    }

    public TagSettings getTagSettings() {
        return this.tagSettings;
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        this.startDetector(drawer);

        boolean detectorRunning = this.detectorThread.isAlive();
        if (detectorRunning) {
            imgui.internal.ImGui.pushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().getAlpha() * 0.5F);
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
        if (detectorRunning) {
            ImGui.popStyleVar();
            imgui.internal.ImGui.popItemFlag();
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            this.detectorThread.interrupt();
            this.getCallback().onClose(this, false);
            this.close();
        }
        if (detectorRunning) {
            ImGui.sameLine();
            ImGui.text("Detecting format...");
        }
    }

    private void startDetector(final ImNbtDrawer drawer) {
        if (this.detectorThread != null) return;

        this.detectorThread = new Thread(() -> {
            Optional<BruteForceDetector.Result> result;
            if (drawer.getConfig().getBoolean(ImNbtConfig.ADVANCED_FORMAT_DETECTION, false)) {
                BruteForceDetector bruteForceDetector = new BruteForceDetector(this.data);
                result = bruteForceDetector.run();
            } else {
                result = Optional.empty();
            }
            result.ifPresentOrElse(r -> {
                this.tagSettings.compressionType = r.compressionType();
                this.tagSettings.endianType = r.endianType();
                this.tagSettings.formatType = r.formatType();
                this.tagSettings.customFormatType = r.customFormatType();
                this.tagSettings.namelessRoot = r.namelessRoot();
                this.unreadBytes = r.unreadBytes();
            }, () -> {
                BasicDetector basicDetector = new BasicDetector(this.data);
                this.tagSettings.compressionType = basicDetector.getCompressionType();
                this.tagSettings.endianType = basicDetector.getEndianType();
                this.tagSettings.formatType = basicDetector.getFormatType();
                this.tagSettings.customFormatType = basicDetector.getCustomFormatType();
            });
            this.copySettingsToUI();
        }, "Format Detector");
        this.detectorThread.setUncaughtExceptionHandler((t, e) -> {}); //Just pretend the current format is the best one
        this.detectorThread.setDaemon(true);
        this.detectorThread.start();
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
