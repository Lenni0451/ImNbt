package net.lenni0451.imnbt.utils;

import javax.annotation.WillNotClose;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    @WillNotClose
    public static byte[] readFully(final InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) baos.write(buf, 0, len);
        return baos.toByteArray();
    }

}
