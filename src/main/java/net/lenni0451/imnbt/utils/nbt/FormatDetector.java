package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

/**
 * A crude format detector for Nbt files.<br>
 * Only works sometimes but it's better than nothing.
 */
public class FormatDetector {

    private static final byte[] GZIP_HEADER = new byte[]{0x1F, (byte) 0x8B};


    private byte[] data;
    private CompressionType compressionType = CompressionType.NO_COMPRESSION;
    private EndianType endianType = EndianType.BIG_ENDIAN;
    private FormatType formatType = FormatType.BEDROCK;

    public FormatDetector(final byte[] data) {
        this.data = data;

        this.detectCompression();
//        this.detectEndian();
        this.detectFormat();
    }

    public CompressionType getCompressionType() {
        return this.compressionType;
    }

    public EndianType getEndianType() {
        return this.endianType;
    }

    public FormatType getFormatType() {
        return this.formatType;
    }

    /**
     * Detect the compression by comparing the first two bytes with the gzip header.
     */
    private void detectCompression() {
        try {
            if (this.data[0] == GZIP_HEADER[0] && this.data[1] == GZIP_HEADER[1]) {
                this.data = CompressionType.GZIP.wrap(new ByteArrayInputStream(this.data)).readAllBytes();
                this.compressionType = CompressionType.GZIP;
            }
        } catch (Throwable ignored) {
        }
    }

    //Not easily possible because most tags can actually read fine with both endian types
//    private void detectEndian() {
//        try {
//            DataInput littleEndian = EndianType.LITTLE_ENDIAN.wrap(new ByteArrayInputStream(this.data));
//            littleEndian.readByte();
//            littleEndian.readUTF();
//            this.endianType = EndianType.LITTLE_ENDIAN;
//        } catch (Throwable ignored) {
//        }
//    }

    /**
     * Detect the format by trying to read the header of a java tag.
     */
    private void detectFormat() {
        try {
            DataInput dataInput = new DataInputStream(new ByteArrayInputStream(this.data));
            dataInput.readByte();
            dataInput.readUTF();
            this.formatType = FormatType.JAVA;
        } catch (Throwable ignored) {
        }
    }

}
