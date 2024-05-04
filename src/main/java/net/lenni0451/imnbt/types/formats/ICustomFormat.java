package net.lenni0451.imnbt.types.formats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface ICustomFormat {

    boolean detect(final byte[] data);

    DataInput read(final DataInput in) throws IOException;

    void write(final DataOutput out, final byte[] data) throws IOException;

}
