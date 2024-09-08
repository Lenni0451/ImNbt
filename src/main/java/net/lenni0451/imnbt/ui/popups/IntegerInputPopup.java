package net.lenni0451.imnbt.ui.popups;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;

import java.util.function.IntConsumer;

/**
 * A popup that allows the user to input an integer.
 */
public class IntegerInputPopup extends Popup<IntegerInputPopup> {

    private final String message;
    private final int minValue;
    private final int maxValue;
    private final IntConsumer intCallback;
    private final int[] value;

    public IntegerInputPopup(final String title, final String message, final int minValue, final int maxValue, final IntConsumer intCallback, final PopupCallback<IntegerInputPopup> callback) {
        super(title, callback);

        this.message = message;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.intCallback = intCallback;
        this.value = new int[]{minValue};
    }

    @Override
    protected void renderContent(ImNbtDrawer drawer) {
        ImGui.textUnformatted(this.message);
        ImGui.sliderInt("##Value", this.value, this.minValue, this.maxValue);
        if (ImGui.button("Ok")) {
            this.close();
            this.getCallback().onClose(this, true);
            this.intCallback.accept(this.value[0]);
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            this.close();
            this.getCallback().onClose(this, false);
        }
    }

}
