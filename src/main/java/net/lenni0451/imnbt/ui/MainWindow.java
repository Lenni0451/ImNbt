package net.lenni0451.imnbt.ui;

import imgui.ImFont;
import imgui.ImGui;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.ui.nbt.*;
import net.lenni0451.imnbt.ui.popups.NewPopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class MainWindow {

    private final Map<NbtType, TagRenderer> nbtRenderer = new EnumMap<>(NbtType.class);
    private final TagSettings tagSettings = new TagSettings();
    private Popup popup = null;
    private INbtTag nbt = null;

    public MainWindow() {
        this.nbtRenderer.put(NbtType.BYTE, new ByteTagRenderer());
        this.nbtRenderer.put(NbtType.SHORT, new ShortTagRenderer());
        this.nbtRenderer.put(NbtType.INT, new IntTagRenderer());
        this.nbtRenderer.put(NbtType.LONG, new LongTagRenderer());
        this.nbtRenderer.put(NbtType.FLOAT, new FloatTagRenderer());
        this.nbtRenderer.put(NbtType.DOUBLE, new DoubleTagRenderer());
        this.nbtRenderer.put(NbtType.BYTE_ARRAY, new ByteArrayTagRenderer());
        this.nbtRenderer.put(NbtType.STRING, new StringTagRenderer());
        this.nbtRenderer.put(NbtType.LIST, new ListTagRenderer());
        this.nbtRenderer.put(NbtType.COMPOUND, new CompoundTagRenderer());
        this.nbtRenderer.put(NbtType.INT_ARRAY, new IntArrayTagRenderer());
        this.nbtRenderer.put(NbtType.LONG_ARRAY, new LongArrayTagRenderer());
    }

    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {

                }
                if (ImGui.menuItem("Save")) {

                }
                if (ImGui.menuItem("New")) {
                    this.popup = new NewPopup(this.tagSettings, success -> {
                        if (success) this.nbt = new CompoundTag();
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

        if (this.nbt == null) ImGui.text("No NBT loaded");
        else this.renderNbt(this.tagSettings.rootName, this.nbt);
        if (this.popup != null) {
            this.popup.open();
            this.popup.render();
        }
    }

    public void renderNbt(final String name, final INbtTag tag) {
        TagRenderer renderer = this.nbtRenderer.get(tag.getNbtType());
        if (renderer == null) {
            ImGui.text("Missing renderer for tag type: " + tag.getNbtType().name());
        } else {
            renderer.render(name, tag);
        }
    }

}
