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
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.FileDialogs;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.UnlimitedReadTracker;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import java.io.*;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.CLOSE;

@SuppressWarnings("unchecked")
public class MainWindow extends Window {

    private static final String ERROR_REQUIRE_TAG = "You need to create or open a Nbt Tag first.";
    private static final String SUCCESS_SAVE = "Successfully saved the Nbt Tag.";
    private static final String ERROR_OPEN = "An unknown error occurred while opening the Nbt Tag.";
    private static final String ERROR_SAVE = "An unknown error occurred while saving the Nbt Tag.";


    private final TagSettings tagSettings = new TagSettings();
    private String previousPath = null;
    private INbtTag tag = null;

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
                    Main.getInstance().getImGuiImpl().openPopup(new SNbtParserPopup((p, success) -> {
                        if (success) this.tag = p.getParsedTag();
                        Main.getInstance().getImGuiImpl().closePopup();
                    }));
                }
                if (ImGui.menuItem("SNbt Serializer")) {
                    if (this.tag == null) Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, CLOSE));
                    else Main.getInstance().getImGuiImpl().openPopup(new SNbtSerializerPopup(this.tag, CLOSE));
                }

                ImGui.endMenu();
            }
            if (ImGui.menuItem("About")) {
                Main.getInstance().getImGuiImpl().openPopup(new AboutPopup((p, success) -> {
                    if (success) {
                        Main.getInstance().getImGuiImpl().closePopup();
                    } else {
                        Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", "An unknown error occurred while opening the URL.", CLOSE));
                    }
                }));
            }

            ImGui.endMenuBar();
        }

        if (this.tag == null) ImGui.text("No NBT loaded");
        else NbtTreeRenderer.render(newName -> this.tagSettings.rootName = newName, () -> this.tag = null, "", this.tagSettings.rootName, this.tag);
    }

    private void open() {
        String response = FileDialogs.open("Open Nbt Tag");
        if (response == null) return;

        byte[] data;
        try (FileInputStream fis = new FileInputStream(response)) {
            data = fis.readAllBytes();
        } catch (Throwable t) {
            t.printStackTrace();
            Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_OPEN, CLOSE));
            return;
        }
        this.open(response, data);
    }

    public void open(final String path, final byte[] data) {
        Main.getInstance().getImGuiImpl().openPopup(new OpenFilePopup(data, this.tagSettings, (p, success) -> {
            if (success) {
                try {
                    DataInput dataInput = this.tagSettings.endianType.wrap(this.tagSettings.compressionType.wrap(new ByteArrayInputStream(data)));
                    this.tagSettings.rootName = "";
                    this.tag = this.tagSettings.formatType.getNbtIO().read(dataInput, UnlimitedReadTracker.INSTANCE);
                    this.previousPath = path;
                    Main.getInstance().getImGuiImpl().closePopup();
                } catch (Throwable t) {
                    t.printStackTrace();
                    Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_OPEN, CLOSE));
                }
            } else {
                Main.getInstance().getImGuiImpl().closePopup();
            }
        }));
    }

    private void save() {
        if (this.tag == null) {
            Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_REQUIRE_TAG, CLOSE));
            return;
        }

        String response = FileDialogs.save("Save Nbt Tag", this.previousPath);
        if (response != null) {
            Main.getInstance().getImGuiImpl().openPopup(new SaveFilePopup(this.tagSettings, (p, success) -> {
                if (success) {
                    try {
                        FileOutputStream fos = new FileOutputStream(response);
                        OutputStream os = this.tagSettings.compressionType.wrap(fos);
                        try (os) {
                            DataOutput dataOutput = this.tagSettings.endianType.wrap(os);
                            this.tagSettings.formatType.getNbtIO().write(dataOutput, this.tagSettings.rootName, this.tag);
                            Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Success", SUCCESS_SAVE, CLOSE));
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Main.getInstance().getImGuiImpl().openPopup(new MessagePopup("Error", ERROR_SAVE, CLOSE));
                    }
                } else {
                    Main.getInstance().getImGuiImpl().closePopup();
                }
            }));
        }
    }

    private void newRootTag() {
        for (NbtType value : NbtType.values()) {
            if (NbtType.END.equals(value)) continue;
            if (ImGui.menuItem(StringUtils.format(value))) {
                Main.getInstance().getImGuiImpl().openPopup(new EditTagPopup("New " + StringUtils.format(value) + " Tag", "Create", "", value.newInstance(), (p, success) -> {
                    if (success) {
                        this.tagSettings.rootName = p.getName();
                        this.tag = p.getTag();
                    }
                    Main.getInstance().getImGuiImpl().closePopup();
                }));
                break;
            }
        }
    }

}
