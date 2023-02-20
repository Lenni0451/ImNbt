package net.lenni0451.imnbt.ui;

import imgui.ImFont;
import imgui.ImGui;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.ui.nbt.*;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.popups.OpenFilePopup;
import net.lenni0451.imnbt.ui.popups.SaveFilePopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.FileDialogs;
import net.lenni0451.imnbt.utils.IOUtils;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.UnlimitedReadTracker;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class MainWindow {

    private static final String ERROR_REQUIRE_TAG = "You need to create or open a Nbt Tag first.";
    private static final String SUCCESS_OPEN = "Successfully opened the Nbt Tag.";
    private static final String SUCCESS_SAVE = "Successfully saved the Nbt Tag.";
    private static final String ERROR_OPEN = "An unknown error occurred while opening the Nbt Tag.";
    private static final String ERROR_SAVE = "An unknown error occurred while saving the Nbt Tag.";


    private final Popup.PopupCallback VOID_CALLBACK = (p, success) -> this.popup = null;
    private final Map<NbtType, TagRenderer> tagRenderer = new EnumMap<>(NbtType.class);
    private final TagSettings tagSettings = new TagSettings();
    private String previousPath = null;
    private Popup<?> popup = null;
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

    public TagRenderer getTagRenderer(@Nonnull final NbtType type) {
        return this.tagRenderer.get(type);
    }

    public void openPopup(final Popup<?> popup) {
        this.popup = popup;
    }

    public void closePopup() {
        this.popup = null;
    }

    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {
                    this.open();
                }
                if (ImGui.menuItem("Save")) {
                    this.save();
                }
                if (ImGui.beginMenu("New Root Tag")) {
                    for (NbtType value : NbtType.values()) {
                        if (NbtType.END.equals(value)) continue;
                        if (ImGui.menuItem(StringUtils.format(value))) {
                            this.popup = new EditTagPopup("New " + StringUtils.format(value) + " Tag", "Create", "", value.newInstance(), (p, success) -> {
                                if (success) {
                                    EditTagPopup editTagPopup = (EditTagPopup) this.popup;
                                    this.tagSettings.rootName = editTagPopup.getName();
                                    this.tag = editTagPopup.getTag();
                                }
                                this.popup = null;
                            });
                            break;
                        }
                    }

                    ImGui.endMenu();
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
        else this.renderNbt(newName -> this.tagSettings.rootName = newName, this.tagSettings.rootName, this.tag);
        if (this.popup != null) {
            this.popup.open();
            this.popup.render();
        }
    }

    public void renderNbt(final Consumer<String> nameEditConsumer, final String name, final INbtTag tag) {
        TagRenderer renderer = this.tagRenderer.get(tag.getNbtType());
        if (renderer == null) {
            ImGui.text("Missing renderer for tag type: " + tag.getNbtType().name());
        } else {
            renderer.render(nameEditConsumer, name.isEmpty() ? "<empty>" : name, tag);
        }
    }

    private void open() {
        String response = FileDialogs.open("Open Nbt Tag");
        if (response == null) return;

        byte[] data;
        try (FileInputStream fis = new FileInputStream(response)) {
            data = IOUtils.readFully(fis);
        } catch (Throwable t) {
            t.printStackTrace();
            this.popup = new MessagePopup("Error", ERROR_OPEN, VOID_CALLBACK);
            return;
        }
        this.popup = new OpenFilePopup(data, this.tagSettings, (p, success) -> {
            if (success) {
                try {
                    DataInput dataInput = this.tagSettings.endianType.wrap(this.tagSettings.compressionType.wrap(new ByteArrayInputStream(data)));
                    this.tagSettings.rootName = "";
                    this.tag = this.tagSettings.formatType.getNbtIO().read(dataInput, UnlimitedReadTracker.INSTANCE);
                    this.previousPath = response;
                    this.popup = new MessagePopup("Success", SUCCESS_OPEN, VOID_CALLBACK);
                } catch (Throwable t) {
                    t.printStackTrace();
                    this.popup = new MessagePopup("Error", ERROR_OPEN, VOID_CALLBACK);
                }
            } else {
                this.popup = null;
            }
        });
    }

    private void save() {
        if (this.tag == null) {
            this.popup = new MessagePopup("Error", ERROR_REQUIRE_TAG, VOID_CALLBACK);
            return;
        }

        String response = FileDialogs.save("Save Nbt Tag", this.previousPath);
        if (response != null) {
            this.popup = new SaveFilePopup(this.tagSettings, (p, success) -> {
                if (success) {
                    try (FileOutputStream fos = new FileOutputStream(response)) {
                        DataOutput dataOutput = this.tagSettings.endianType.wrap(this.tagSettings.compressionType.wrap(fos));
                        this.tagSettings.formatType.getNbtIO().write(dataOutput, this.tagSettings.rootName, this.tag);
                        this.popup = new MessagePopup("Success", SUCCESS_SAVE, VOID_CALLBACK);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        this.popup = new MessagePopup("Error", ERROR_SAVE, VOID_CALLBACK);
                    }
                } else {
                    this.popup = null;
                }
            });
        }
    }

}
