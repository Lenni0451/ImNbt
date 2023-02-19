package net.lenni0451.imnbt;

import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;

public class TagSettings {

    public String rootName = "";
    public FormatType formatType = FormatType.JAVA;
    public EndianType endianType = EndianType.BIG_ENDIAN;
    public CompressionType compressionType = CompressionType.NO_COMPRESSION;

}
