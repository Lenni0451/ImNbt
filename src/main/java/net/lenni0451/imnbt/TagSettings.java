package net.lenni0451.imnbt;

import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;

/**
 * The {@link #formatType}, {@link #endianType} and {@link #compressionType} are only used to select default values when saving a tag.<br>
 * If a tag is newly created, the default values are used when showing the save dialog.
 */
public class TagSettings {

    /**
     * The name of the root tag.
     */
    public String rootName = "";
    /**
     * The current format of the tag.
     */
    public FormatType formatType = FormatType.JAVA;
    /**
     * The current endian type of the tag.
     */
    public EndianType endianType = EndianType.BIG_ENDIAN;
    /**
     * The current compression type of the tag.
     */
    public CompressionType compressionType = CompressionType.NO_COMPRESSION;

}
