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
    private final Number minValue;
    private final Number maxValue;
    private boolean contextMenuActive;

    public ImGuiNumberPicker(final Class<? extends Number> type) {
        this.type = type;
        this.minValue = this.getMinValue();
        this.maxValue = this.getMaxValue();
    }

    public Number render(Number value) {
        final int width = (int) ImGui.calcItemWidth();
        final int buttonSize = (int) ImGui.getTextLineHeight() + 6;

        this.input.set(FORMAT.format(value));
        ImGui.setNextItemWidth(width - buttonSize * 2 - 8);
        if (ImGui.inputText("##Value", this.input, ImGuiInputTextFlags.CharsDecimal | ImGuiInputTextFlags.CharsNoBlank)) {
            value = this.parse(value, this.input.get());
        }
        if (ImGui.isItemActive() || ImGui.isItemHovered()) this.contextMenuActive = true;
        if (this.contextMenuActive && ImGui.beginPopupContextWindow()) {
            if (ImGui.selectable("Reset")) {
                value = 0;
            }
            if (ImGui.selectable("Min Value")) {
                value = this.minValue;
            }
            if (ImGui.selectable("Max Value")) {
                value = this.maxValue;
            }

            ImGui.endPopup();
        } else {
            this.contextMenuActive = false;
        }
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() - 4);
        if (ImGui.button("-", buttonSize, buttonSize)) {
            value = this.decrement(value);
        }
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() - 4);
        if (ImGui.button("+", buttonSize, buttonSize)) {
            value = this.increment(value);
        }
        ImGui.sameLine();
        ImGui.text("Value");
        return value;
    }

    private Number parse(final Number def, final String value) {
        if (byte.class.equals(this.type)) {
            try {
                return Byte.parseByte(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (short.class.equals(this.type)) {
            try {
                return Short.parseShort(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (int.class.equals(this.type)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (long.class.equals(this.type)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (float.class.equals(this.type)) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (double.class.equals(this.type)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }

    private Number increment(final Number number) {
        if (byte.class.equals(this.type)) {
            byte b = number.byteValue();
            if (b == Byte.MAX_VALUE) return b;
            return b + 1;
        } else if (short.class.equals(this.type)) {
            short s = number.shortValue();
            if (s == Short.MAX_VALUE) return s;
            return s + 1;
        } else if (int.class.equals(this.type)) {
            int i = number.intValue();
            if (i == Integer.MAX_VALUE) return i;
            return i + 1;
        } else if (long.class.equals(this.type)) {
            long l = number.longValue();
            if (l == Long.MAX_VALUE) return l;
            return l + 1;
        } else if (float.class.equals(this.type)) {
            float f = number.floatValue();
            if (f == Float.MAX_VALUE) return f;
            return f + 1;
        } else if (double.class.equals(this.type)) {
            double d = number.doubleValue();
            if (d == Double.MAX_VALUE) return d;
            return d + 1;
        }
        return number;
    }

    private Number decrement(final Number number) {
        if (byte.class.equals(this.type)) {
            byte b = number.byteValue();
            if (b == Byte.MIN_VALUE) return b;
            return b - 1;
        } else if (short.class.equals(this.type)) {
            short s = number.shortValue();
            if (s == Short.MIN_VALUE) return s;
            return s - 1;
        } else if (int.class.equals(this.type)) {
            int i = number.intValue();
            if (i == Integer.MIN_VALUE) return i;
            return i - 1;
        } else if (long.class.equals(this.type)) {
            long l = number.longValue();
            if (l == Long.MIN_VALUE) return l;
            return l - 1;
        } else if (float.class.equals(this.type)) {
            float f = number.floatValue();
            if (f == Float.MIN_VALUE) return f;
            return f - 1;
        } else if (double.class.equals(this.type)) {
            double d = number.doubleValue();
            if (d == Double.MIN_VALUE) return d;
            return d - 1;
        }
        return number;
    }

    private Number getMinValue() {
        if (byte.class.equals(this.type)) {
            return Byte.MIN_VALUE;
        } else if (short.class.equals(this.type)) {
            return Short.MIN_VALUE;
        } else if (int.class.equals(this.type)) {
            return Integer.MIN_VALUE;
        } else if (long.class.equals(this.type)) {
            return Long.MIN_VALUE;
        } else if (float.class.equals(this.type)) {
            return Float.MIN_VALUE;
        } else if (double.class.equals(this.type)) {
            return Double.MIN_VALUE;
        }
        return null;
    }

    private Number getMaxValue() {
        if (byte.class.equals(this.type)) {
            return Byte.MAX_VALUE;
        } else if (short.class.equals(this.type)) {
            return Short.MAX_VALUE;
        } else if (int.class.equals(this.type)) {
            return Integer.MAX_VALUE;
        } else if (long.class.equals(this.type)) {
            return Long.MAX_VALUE;
        } else if (float.class.equals(this.type)) {
            return Float.MAX_VALUE;
        } else if (double.class.equals(this.type)) {
            return Double.MAX_VALUE;
        }
        return null;
    }

}
