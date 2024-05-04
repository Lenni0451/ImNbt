package net.lenni0451.imnbt.types.formats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NoneFormat implements ICustomFormat {

    @Override
    public boolean detect(byte[] data) {
        return false;
    }

    @Override
    public DataInput read(DataInput in) throws IOException {
        return in;
    }

    @Override
    public void write(DataOutput out, byte[] data) throws IOException {
        out.write(data);
    }

}
