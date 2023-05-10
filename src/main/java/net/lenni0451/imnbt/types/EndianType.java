package net.lenni0451.imnbt.types;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.ThrowingFunction;

import java.io.*;

/**
 * The endian type of the Nbt tag.
 */
@SuppressWarnings("UnstableApiUsage")
public enum EndianType {

    BIG_ENDIAN(DataInputStream::new, DataOutputStream::new),
    LITTLE_ENDIAN(LittleEndianDataInputStream::new, LittleEndianDataOutputStream::new);


    public static final String[] NAMES = new String[values().length];

    static {
        for (int i = 0; i < values().length; i++) NAMES[i] = StringUtils.format(values()[i]);
    }


    private final ThrowingFunction<InputStream, DataInput> inputWrapper;
    private final ThrowingFunction<OutputStream, DataOutput> outputWrapper;

    EndianType(final ThrowingFunction<InputStream, DataInput> inputWrapper, final ThrowingFunction<OutputStream, DataOutput> outputWrapper) {
        this.inputWrapper = inputWrapper;
        this.outputWrapper = outputWrapper;
    }

    public DataInput wrap(final InputStream is) throws Throwable {
        return this.inputWrapper.apply(is);
    }

    public DataOutput wrap(final OutputStream os) throws Throwable {
        return this.outputWrapper.apply(os);
    }

}
