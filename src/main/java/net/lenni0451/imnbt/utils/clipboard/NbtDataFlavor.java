package net.lenni0451.imnbt.utils.clipboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

/**
 * Nbt data flavor for the clipboard.<br>
 * This is used to check if the clipboard contains a nbt tag.
 */
public class NbtDataFlavor extends DataFlavor {

    public static final NbtDataFlavor INSTANCE = new NbtDataFlavor();

    /**
     * Check if the clipboard contains a nbt tag.
     *
     * @param clipboard The clipboard to check
     * @return If the clipboard contains a nbt tag
     */
    public static boolean isInClipboard(final Clipboard clipboard) {
        return clipboard.isDataFlavorAvailable(INSTANCE);
    }

    /**
     * Check if the system clipboard contains a nbt tag.
     *
     * @return If the system clipboard contains a nbt tag
     */
    public static boolean isInSystemClipboard() {
        return isInClipboard(Toolkit.getDefaultToolkit().getSystemClipboard());
    }


    public NbtDataFlavor() {
        super(NbtClipboardContent.class, "Nbt Tag");
    }

}
