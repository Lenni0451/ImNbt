package net.lenni0451.imnbt.utils.stream;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class LittleEndianDataInput implements DataInput {

    private final ByteBuffer buffer;

    public LittleEndianDataInput(final InputStream is) throws IOException {
        this.buffer = ByteBuffer.wrap(is.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void readFully(byte[] b) {
        this.buffer.get(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) {
        this.buffer.get(b, off, len);
    }

    @Override
    public int skipBytes(int n) {
        this.buffer.position(this.buffer.position() + n);
        return n;
    }

    @Override
    public boolean readBoolean() {
        return this.buffer.get() != 0;
    }

    @Override
    public byte readByte() {
        return this.buffer.get();
    }

    @Override
    public int readUnsignedByte() {
        return this.buffer.get() & 0xFF;
    }

    @Override
    public short readShort() {
        return this.buffer.getShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.buffer.getShort() & 0xFFFF;
    }

    @Override
    public char readChar() {
        return this.buffer.getChar();
    }

    @Override
    public int readInt() {
        return this.buffer.getInt();
    }

    @Override
    public long readLong() {
        return this.buffer.getLong();
    }

    @Override
    public float readFloat() {
        return this.buffer.getFloat();
    }

    @Override
    public double readDouble() {
        return this.buffer.getDouble();
    }

    @Override
    public String readLine() {
        StringBuilder sb = new StringBuilder();
        char c;
        while (this.buffer.hasRemaining()) {
            c = (char) this.readUnsignedByte();
            if (c == '\n') {
                break;
            } else if (c == '\r') {
                char next = (char) this.buffer.get();
                if (next != '\n') this.buffer.position(this.buffer.position() - 1);
                break;
            } else {
                sb.append(c);
            }
        }
        if (sb.isEmpty() && !this.buffer.hasRemaining()) return null;
        return sb.toString();
    }

    @Override
    public String readUTF() {
        int length = this.readUnsignedShort();
        byte[] bytes = new byte[length];
        this.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public int available() {
        return this.buffer.remaining();
    }

}
