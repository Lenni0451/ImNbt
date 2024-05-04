package net.lenni0451.imnbt.types.formats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BedrockLevelDatFormat implements ICustomFormat {

    private int version;

    @Override
    public boolean detect(byte[] data) {
        if (data.length < 8) return false;
        long length = (long) (data[4] & 255) | (long) (data[5] & 255) << 8 | (long) (data[6] & 255) << 16 | (long) (data[7] & 255) << 24;
        return length == data.length - 8;
    }

    @Override
    public DataInput read(DataInput in) throws IOException {
        this.version = in.readInt();
        in.readInt(); //Data length
        return in;
    }

    @Override
    public void write(DataOutput out, byte[] data) throws IOException {
        out.writeInt(this.version);
        out.writeInt(data.length);
        out.write(data);
    }

}
