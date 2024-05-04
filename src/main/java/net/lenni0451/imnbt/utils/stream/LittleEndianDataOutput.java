package net.lenni0451.imnbt.utils.stream;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LittleEndianDataOutput implements DataOutput {

    private final OutputStream os;

    public LittleEndianDataOutput(final OutputStream os) {
        this.os = os;
    }

    @Override
    public void write(int b) throws IOException {
        this.os.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.os.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.os.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.os.write(v ? 1 : 0);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.os.write(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.os.write(v & 0xFF);
        this.os.write((v >> 8) & 0xFF);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.os.write(v & 0xFF);
        this.os.write((v >> 8) & 0xFF);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.os.write(v & 0xFF);
        this.os.write((v >> 8) & 0xFF);
        this.os.write((v >> 16) & 0xFF);
        this.os.write((v >> 24) & 0xFF);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.os.write((int) (v & 0xFF));
        this.os.write((int) ((v >> 8) & 0xFF));
        this.os.write((int) ((v >> 16) & 0xFF));
        this.os.write((int) ((v >> 24) & 0xFF));
        this.os.write((int) ((v >> 32) & 0xFF));
        this.os.write((int) ((v >> 40) & 0xFF));
        this.os.write((int) ((v >> 48) & 0xFF));
        this.os.write((int) ((v >> 56) & 0xFF));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.write(s.getBytes());
    }

    @Override
    public void writeChars(String s) throws IOException {
        for (char c : s.toCharArray()) this.writeChar(c);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        this.writeShort(bytes.length);
        this.write(bytes);
    }

}
