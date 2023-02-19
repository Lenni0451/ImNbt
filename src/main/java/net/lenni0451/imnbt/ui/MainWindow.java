package net.lenni0451.imnbt.ui;

import imgui.ImFont;
import imgui.ImGui;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.nbt.*;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class MainWindow {

    private final Map<NbtType, TagRenderer> nbtRenderer = new EnumMap<>(NbtType.class);
    private INbtTag nbt;

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

        CompoundTag tag = new CompoundTag();
        tag.addByte("byte", (byte) 12);
        tag.addShort("short", (short) 123);
        tag.addInt("int", 1234);
        tag.addLong("long", 12345L);
        tag.addFloat("float", 123.456F);
        tag.addDouble("double", 123.456);
        tag.addByteArray("byteArray", new byte[]{1, 2, 3, 4, 5});
        tag.addString("string", "Hello World");
        tag.addList("list", 1, 2, 3, 4, 5);
        {
            CompoundTag subTag = new CompoundTag();
            subTag.addString("subString", "Hello World");
            tag.addCompound("compound", subTag);
        }
        tag.addIntArray("intArray", 1, 2, 3, 4, 5);
        tag.addLongArray("longArray", 1L, 2L, 3L, 4L, 5L);

        this.nbt = tag;
    }

    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {

                }
                if (ImGui.menuItem("Save")) {

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

        this.renderNbt("Test", this.nbt);
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
