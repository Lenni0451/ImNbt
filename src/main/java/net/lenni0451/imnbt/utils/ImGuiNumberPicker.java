package net.lenni0451.imnbt.utils;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.text.DecimalFormat;

public class ImGuiNumberPicker {

    private static final DecimalFormat FORMAT = new DecimalFormat();

    static {
        FORMAT.setGroupingUsed(false);
    }


    private final ImString input = new ImString(128);
    private final Class<? extends Number> type;

    public ImGuiNumberPicker(final Class<? extends Number> type) {
        this.type = type;
    }

    public Number render(final Number value) {
        this.input.set(FORMAT.format(value));
        if (ImGui.inputText("Value", this.input, ImGuiInputTextFlags.CharsDecimal | ImGuiInputTextFlags.CharsNoBlank)) {
            if (byte.class.equals(this.type)) {
                try {
                    return Byte.parseByte(this.input.get());
                } catch (NumberFormatException ignored) {
                }
            } else if (short.class.equals(this.type)) {
                try {
                    return Short.parseShort(this.input.get());
                } catch (NumberFormatException ignored) {
                }
            } else if (int.class.equals(this.type)) {
                try {
                    return Integer.parseInt(this.input.get());
                } catch (NumberFormatException ignored) {
                }
            } else if (long.class.equals(this.type)) {
                try {
                    return Long.parseLong(this.input.get());
                } catch (NumberFormatException ignored) {
                }
            } else if (float.class.equals(this.type)) {
                try {
                    return Float.parseFloat(this.input.get());
                } catch (NumberFormatException ignored) {
                }
            } else if (double.class.equals(this.type)) {
                try {
                    return Double.parseDouble(this.input.get());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return value;
    }

}
