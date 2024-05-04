package net.lenni0451.imnbt.types;

import net.lenni0451.imnbt.types.formats.BedrockLevelDatFormat;
import net.lenni0451.imnbt.types.formats.ICustomFormat;
import net.lenni0451.imnbt.types.formats.NoneFormat;
import net.lenni0451.imnbt.utils.StringUtils;

import java.util.List;
import java.util.function.Supplier;

public enum CustomFormatType {

    NONE(NoneFormat::new),
    BEDROCK_LEVEL_DAT(BedrockLevelDatFormat::new, FormatType.JAVA, EndianType.LITTLE_ENDIAN);


    public static final String[] NAMES = new String[values().length];

    static {
        for (int i = 0; i < values().length; i++) NAMES[i] = StringUtils.format(values()[i]);
    }


    private final Supplier<ICustomFormat> formatSupplier;
    private final List<Enum<?>> defaultSettings;

    CustomFormatType(final Supplier<ICustomFormat> formatSupplier, final Enum<?>... defaultSettings) {
        this.formatSupplier = formatSupplier;
        this.defaultSettings = List.of(defaultSettings);
    }

    public ICustomFormat createFormat() {
        return this.formatSupplier.get();
    }

    public List<Enum<?>> getDefaultSettings() {
        return this.defaultSettings;
    }

}
