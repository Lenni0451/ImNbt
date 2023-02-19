package net.lenni0451.imnbt.types;

import net.lenni0451.imnbt.utils.StringUtils;

public enum CompressionType {

    NO_COMPRESSION, GZIP;


    public static final String[] NAMES = new String[values().length];

    static {
        for (int i = 0; i < values().length; i++) NAMES[i] = StringUtils.format(values()[i]);
    }

}
