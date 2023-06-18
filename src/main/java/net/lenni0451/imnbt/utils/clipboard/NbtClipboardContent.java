package net.lenni0451.imnbt.utils.clipboard;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.io.NamedTag;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * Nbt clipboard content.<br>
 * Contains the nbt tag that is in the clipboard.
 */
public class NbtClipboardContent implements Transferable, ClipboardOwner {

    /**
     * Get the nbt tag from the clipboard.
     *
     * @param clipboard The clipboard to get the tag from
     * @return The nbt tag
     * @throws UnsupportedFlavorException If the clipboard does not contain a nbt tag
     * @throws IOException                If a serialization error occurs
     */
    public static NamedTag getFromClipboard(final Clipboard clipboard) throws UnsupportedFlavorException, IOException {
        return (NamedTag) clipboard.getData(NbtDataFlavor.INSTANCE);
    }

    /**
     * Get the nbt tag from the system clipboard.
     *
     * @return The nbt tag
     * @throws UnsupportedFlavorException If the clipboard does not contain a nbt tag
     * @throws IOException                If a serialization error occurs
     */
    public static NamedTag getFromSystemClipboard() throws UnsupportedFlavorException, IOException {
        return getFromClipboard(Toolkit.getDefaultToolkit().getSystemClipboard());
    }


    private final NamedTag tag;

    public NbtClipboardContent(final String name, final INbtTag tag) {
        this.tag = new NamedTag(name, tag.getNbtType(), tag);
    }

    /**
     * Set the clipboard contents and owner to this instance.
     *
     * @param clipboard The clipboard to set
     */
    public void setContents(final Clipboard clipboard) {
        clipboard.setContents(this, this);
    }

    /**
     * Set the system clipboard contents and owner to this instance.
     */
    public void setSystemClipboard() {
        this.setContents(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{NbtDataFlavor.INSTANCE};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor instanceof NbtDataFlavor;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!this.isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
        return this.tag;
    }

}
