package net.lenni0451.imnbt.utils;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream implements DataInput {

    private final InputStream in;

    public LittleEndianDataInputStream(final InputStream in) {
        this.in = in;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.in.readNBytes(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.in.readNBytes(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return (int) this.in.skip(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.in.read() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) this.in.read();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.in.read();
    }

    @Override
    public short readShort() throws IOException {
        return (short) (this.in.read() | this.in.read() << 8);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.in.read() | this.in.read() << 8;
    }

    @Override
    public char readChar() throws IOException {
        return (char) (this.in.read() | this.in.read() << 8);
    }

    @Override
    public int readInt() throws IOException {
        return this.in.read()
                | this.in.read() << 8
                | this.in.read() << 16
                | this.in.read() << 24;
    }

    @Override
    public long readLong() throws IOException {
        return (long) (this.in.read() & 255)
                | (long) (this.in.read() & 255) << 8
                | (long) (this.in.read() & 255) << 16
                | (long) (this.in.read() & 255) << 24
                | (long) (this.in.read() & 255) << 32
                | (long) (this.in.read() & 255) << 40
                | (long) (this.in.read() & 255) << 48
                | (long) (this.in.read() & 255) << 56;
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public String readLine() {
        throw new UnsupportedOperationException("readLine is not supported");
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

}
