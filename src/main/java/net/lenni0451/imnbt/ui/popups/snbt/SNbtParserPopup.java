package net.lenni0451.imnbt.ui.popups.snbt;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.imnbt.ImGuiImpl;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.snbt.SNbtSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class SNbtParserPopup extends Popup<SNbtParserPopup> {

    private final ImString input = new ImString(32767);
    private final Map<String, SNbtSerializer<?>> versions = this.getVersions();
    private final String[] versionNames = this.getVersionNames();
    private final ImInt selectedVersion = new ImInt(this.versionNames.length - 1);
    private INbtTag parsedTag;

    public SNbtParserPopup(final PopupCallback<SNbtParserPopup> callback) {
        super("SNbt Parser", callback);
    }

    public INbtTag getParsedTag() {
        return this.parsedTag;
    }

    @Override
    protected void renderContent() {
        ImGui.combo("Version", this.selectedVersion, this.versionNames);
        ImGui.inputText("Input", this.input);

        if (ImGui.button("Parse")) {
            this.close();
            try {
                this.parsedTag = this.versions.get("V" + this.versionNames[this.selectedVersion.get()].replace('.', '_')).deserialize(this.input.get());
                this.getCallback().onClose(this, true);
            } catch (Throwable t) {
                ImGuiImpl.getInstance().getMainWindow().openPopup(new MessagePopup("Error", t.getMessage(), (p, success) -> ImGuiImpl.getInstance().getMainWindow().openPopup(this)));
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            this.getCallback().onClose(this, false);
            this.close();
        }
    }

    private Map<String, SNbtSerializer<?>> getVersions() {
        Map<String, SNbtSerializer<?>> versions = new LinkedHashMap<>();
        try {
            for (Field field : SNbtSerializer.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && SNbtSerializer.class.isAssignableFrom(field.getType())) {
                    versions.put(field.getName(), (SNbtSerializer<?>) field.get(null));
                }
            }
        } catch (Throwable ignored) {
        }
        return versions;
    }

    private String[] getVersionNames() {
        String[] names = new String[this.versions.size()];
        int i = 0;
        for (String name : this.versions.keySet()) names[i++] = name.substring(1).replace('_', '.');
        return names;
    }

}
