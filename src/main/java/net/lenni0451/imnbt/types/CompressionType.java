package net.lenni0451.imnbt.types;

import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.ThrowingFunction;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public ThrowingFunction<InputStream, InputStream> getInputWrapper() {
        return this.inputWrapper;
    }

    public ThrowingFunction<OutputStream, OutputStream> getOutputWrapper() {
        return this.outputWrapper;
    }

}
