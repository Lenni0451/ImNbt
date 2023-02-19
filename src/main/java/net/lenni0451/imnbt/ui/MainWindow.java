package net.lenni0451.imnbt.ui;

import imgui.ImFont;
import imgui.ImGui;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.ui.nbt.*;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.popups.NewPopup;
import net.lenni0451.imnbt.ui.popups.OpenPopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.FileDialogs;
import net.lenni0451.imnbt.utils.UnlimitedReadTracker;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EnumMap;
import java.util.Map;

public class MainWindow {

    private final Popup.PopupCallback VOID_CALLBACK = success -> this.popup = null;
    private final Map<NbtType, TagRenderer> tagRenderer = new EnumMap<>(NbtType.class);
    private final TagSettings tagSettings = new TagSettings();
    private Popup popup = null;
    private INbtTag tag = null;

    public MainWindow() {
        this.tagRenderer.put(NbtType.BYTE, new ByteTagRenderer());
        this.tagRenderer.put(NbtType.SHORT, new ShortTagRenderer());
        this.tagRenderer.put(NbtType.INT, new IntTagRenderer());
        this.tagRenderer.put(NbtType.LONG, new LongTagRenderer());
        this.tagRenderer.put(NbtType.FLOAT, new FloatTagRenderer());
        this.tagRenderer.put(NbtType.DOUBLE, new DoubleTagRenderer());
        this.tagRenderer.put(NbtType.BYTE_ARRAY, new ByteArrayTagRenderer());
        this.tagRenderer.put(NbtType.STRING, new StringTagRenderer());
        this.tagRenderer.put(NbtType.LIST, new ListTagRenderer());
        this.tagRenderer.put(NbtType.COMPOUND, new CompoundTagRenderer());
        this.tagRenderer.put(NbtType.INT_ARRAY, new IntArrayTagRenderer());
        this.tagRenderer.put(NbtType.LONG_ARRAY, new LongArrayTagRenderer());
    }

    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {
                    String response = FileDialogs.open("Open Nbt Tag");
                    if (response != null) {
                        this.popup = new OpenPopup(this.tagSettings, success -> {
                            if (success) {
                                try (FileInputStream fis = new FileInputStream(response)) {
                                    DataInput dataInput = this.tagSettings.endianType.getInputWrapper().apply(this.tagSettings.compressionType.getInputWrapper().apply(fis));
                                    this.tagSettings.rootName = "";
                                    this.tag = this.tagSettings.formatType.getNbtIO().read(dataInput, UnlimitedReadTracker.INSTANCE);
                                    this.popup = new MessagePopup("Success", "Successfully opened the NBT tag.", VOID_CALLBACK);
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                    this.popup = new MessagePopup("Error", "An unknown error occurred whilst opening the tag.", VOID_CALLBACK);
                                }
                            } else {
                                this.popup = null;
                            }
                        });
                    }
                }
                if (ImGui.menuItem("Save")) {
                    if (this.tag == null) {
                        this.popup = new MessagePopup("Error", "You first to need to open or create a tag.", VOID_CALLBACK);
                    } else {
                        String response = FileDialogs.save("Save Nbt Tag", null);
                        if (response != null) {
                            try (FileOutputStream fos = new FileOutputStream(response)) {
                                DataOutput dataOutput = this.tagSettings.endianType.getOutputWrapper().apply(this.tagSettings.compressionType.getOutputWrapper().apply(fos));
                                this.tagSettings.formatType.getNbtIO().write(dataOutput, this.tagSettings.rootName, this.tag);
                                this.popup = new MessagePopup("Success", "Successfully saved the NBT tag.", VOID_CALLBACK);
                            } catch (Throwable t) {
                                t.printStackTrace();
                                this.popup = new MessagePopup("Error", "An unknown error occurred whilst saving the tag.", VOID_CALLBACK);
                            }
                        }
                    }
                }
                if (ImGui.menuItem("New")) {
                    this.popup = new NewPopup(this.tagSettings, success -> {
                        if (success) this.tag = new CompoundTag();
                        this.popup = null;
                    });
                }
                ImGui.separator();
                if (ImGui.menuItem("Exit")) {
                    System.exit(0);
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Font Size")) {
                ImFont[] fonts = ImGuiImpl.getInstance().getFonts();
                int usedFont = ImGuiImpl.getInstance().getUsedFont();
                for (int i = 0; i < fonts.length; i++) {
                    ImFont font = fonts[i];
                    if (ImGui.menuItem("Size " + String.format("%.0f", font.getFontSize()), "", i == usedFont)) {
                        ImGuiImpl.getInstance().setUsedFont(i);
                    }
                }

                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        if (this.tag == null) ImGui.text("No NBT loaded");
        else this.renderNbt(this.tagSettings.rootName.isEmpty() ? "<root>" : this.tagSettings.rootName, this.tag);
        if (this.popup != null) {
            this.popup.open();
            this.popup.render();
        }
    }

    public void renderNbt(final String name, final INbtTag tag) {
        TagRenderer renderer = this.tagRenderer.get(tag.getNbtType());
        if (renderer == null) {
            ImGui.text("Missing renderer for tag type: " + tag.getNbtType().name());
        } else {
            renderer.render(name, tag);
        }
    }

}
