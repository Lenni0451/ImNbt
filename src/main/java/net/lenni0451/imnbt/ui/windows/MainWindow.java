package net.lenni0451.imnbt.ui.windows;

import imgui.ImFont;
import imgui.ImGui;
import net.lenni0451.imnbt.Main;
import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.popups.AboutPopup;
import net.lenni0451.imnbt.ui.popups.EditTagPopup;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.popups.file.OpenFilePopup;
import net.lenni0451.imnbt.ui.popups.file.SaveFilePopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtParserPopup;
import net.lenni0451.imnbt.ui.popups.snbt.SNbtSerializerPopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.FileDialogs;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.UnlimitedReadTracker;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import java.io.*;

@SuppressWarnings("unchecked")
public class MainWindow extends Window {

    private static final String ERROR_REQUIRE_TAG = "You need to create or open a Nbt Tag first.";
    private static final String SUCCESS_SAVE = "Successfully saved the Nbt Tag.";
    private static final String ERROR_OPEN = "An unknown error occurred while opening the Nbt Tag.";
    private static final String ERROR_SAVE = "An unknown error occurred while saving the Nbt Tag.";


    private final Popup.PopupCallback VOID_CALLBACK = (p, success) -> this.popup = null;
    private final TagSettings tagSettings = new TagSettings();
    private String previousPath = null;
    private Popup<?> popup = null;
    private INbtTag tag = null;

    public void openPopup(final Popup<?> popup) {
        this.popup = popup;
    }

    public void closePopup() {
        this.popup = null;
    }

    @Override
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
                    this.newRootTag();

                    ImGui.endMenu();
                }
                ImGui.separator();
                if (ImGui.menuItem("Exit")) {
                    System.exit(0);
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Font Size")) {
                ImFont[] fonts = Main.getInstance().getConfig().getFonts();
                int usedFont = Main.getInstance().getConfig().getUsedFont();
                for (int i = 0; i < fonts.length; i++) {
                    ImFont font = fonts[i];
                    if (ImGui.menuItem("Size " + String.format("%.0f", font.getFontSize()), "", i == usedFont)) {
                        Main.getInstance().getConfig().setUsedFont(i);
                    }
                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("SNbt")) {
                if (ImGui.menuItem("SNbt Parser")) {
                    this.popup = new SNbtParserPopup((p, success) -> {
                        if (success) this.tag = p.getParsedTag();
                        this.popup = null;
                    });
                }
                if (ImGui.menuItem("SNbt Serializer")) {
                    if (this.tag == null) this.popup = new MessagePopup("Error", ERROR_REQUIRE_TAG, VOID_CALLBACK);
                    else this.popup = new SNbtSerializerPopup(this.tag, VOID_CALLBACK);
                }

                ImGui.endMenu();
            }
            if (ImGui.menuItem("About")) {
                this.popup = new AboutPopup((p, success) -> {
                    if (success) {
                        this.popup = null;
                    } else {
                        this.popup = new MessagePopup("Error", "An unknown error occurred while opening the URL.", VOID_CALLBACK);
                    }
                });
            }

            ImGui.endMenuBar();
        }

        if (this.tag == null) ImGui.text("No NBT loaded");
        else NbtTreeRenderer.render(newName -> this.tagSettings.rootName = newName, () -> this.tag = null, "", this.tagSettings.rootName, this.tag);
        if (this.popup != null) {
            this.popup.open();
            this.popup.render();
        }
    }

    private void open() {
        String response = FileDialogs.open("Open Nbt Tag");
        if (response == null) return;

        byte[] data;
        try (FileInputStream fis = new FileInputStream(response)) {
            data = fis.readAllBytes();
        } catch (Throwable t) {
            t.printStackTrace();
            this.popup = new MessagePopup("Error", ERROR_OPEN, VOID_CALLBACK);
            return;
        }
        this.open(response, data);
    }

    public void open(final String path, final byte[] data) {
        this.popup = new OpenFilePopup(data, this.tagSettings, (p, success) -> {
            if (success) {
                try {
                    DataInput dataInput = this.tagSettings.endianType.wrap(this.tagSettings.compressionType.wrap(new ByteArrayInputStream(data)));
                    this.tagSettings.rootName = "";
                    this.tag = this.tagSettings.formatType.getNbtIO().read(dataInput, UnlimitedReadTracker.INSTANCE);
                    this.previousPath = path;
                    this.popup = null;
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
                    try {
                        FileOutputStream fos = new FileOutputStream(response);
                        OutputStream os = this.tagSettings.compressionType.wrap(fos);
                        try (os) {
                            DataOutput dataOutput = this.tagSettings.endianType.wrap(os);
                            this.tagSettings.formatType.getNbtIO().write(dataOutput, this.tagSettings.rootName, this.tag);
                            this.popup = new MessagePopup("Success", SUCCESS_SAVE, VOID_CALLBACK);
                        }
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

    private void newRootTag() {
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
    }

}
