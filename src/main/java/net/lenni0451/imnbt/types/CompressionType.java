package net.lenni0451.imnbt.types;

import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.ThrowingFunction;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The compression type of the Nbt tag.
 */
public enum CompressionType {

    NO_COMPRESSION(is -> is, os -> os),
    GZIP(GZIPInputStream::new, GZIPOutputStream::new);


    public static final String[] NAMES = new String[values().length];

    static {
        for (int i = 0; i < values().length; i++) NAMES[i] = StringUtils.format(values()[i]);
    }


    private final ThrowingFunction<InputStream, InputStream> inputWrapper;
    private final ThrowingFunction<OutputStream, OutputStream> outputWrapper;

    CompressionType(final ThrowingFunction<InputStream, InputStream> inputWrapper, final ThrowingFunction<OutputStream, OutputStream> outputWrapper) {
        this.inputWrapper = inputWrapper;
        this.outputWrapper = outputWrapper;
    }

    public InputStream wrap(final InputStream is) throws Throwable {
        return this.inputWrapper.apply(is);
    }

    public OutputStream wrap(final OutputStream os) throws Throwable {
        return this.outputWrapper.apply(os);
    }

}
