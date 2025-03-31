package net.lenni0451.imnbt.types;

import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.mcstructs.nbt.io.NbtIO;
import net.lenni0451.mcstructs_bedrock.nbt.io.BedrockNbtIO;

/**
 * The format type of the Nbt tag.
 */
public enum FormatType {

    JAVA(NbtIO.LATEST),
    BEDROCK(BedrockNbtIO.INSTANCE);


    public static final String[] NAMES = new String[values().length];

    static {
        for (int i = 0; i < values().length; i++) NAMES[i] = StringUtils.format(values()[i]);
    }


    private final NbtIO nbtIO;

    FormatType(final NbtIO nbtIO) {
        this.nbtIO = nbtIO;
    }

    public NbtIO getNbtIO() {
        return this.nbtIO;
    }

}
