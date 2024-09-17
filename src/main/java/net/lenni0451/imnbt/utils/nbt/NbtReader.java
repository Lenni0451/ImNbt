package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.imnbt.TagSettings;
import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.CustomFormatType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.types.formats.ICustomFormat;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.io.NamedTag;
import net.lenni0451.mcstructs.nbt.io.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.util.LinkedHashMap;
import java.util.Optional;

public class NbtReader {

    public static ReadResult read(final byte[] data, final NbtReadTracker readTracker, final TagSettings tagSettings) throws Throwable {
        return read(data, readTracker, tagSettings.compressionType, tagSettings.endianType, tagSettings.formatType, tagSettings.customFormatType, tagSettings.namelessRoot, tagSettings.readExtraData);
    }

    public static ReadResult read(final byte[] data, final NbtReadTracker readTracker, final CompressionType compressionType, final EndianType endianType, final FormatType formatType, final CustomFormatType customFormatType, final boolean namelessRoot, final boolean readExtraData) throws Throwable {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInput dataInput = endianType.wrap(compressionType.wrap(bais));
        ICustomFormat customFormat = customFormatType.createFormat();
        dataInput = customFormat.read(dataInput);
        NamedTag namedTag;
        if (namelessRoot) {
            INbtTag namelessTag = formatType.getNbtIO().readUnnamed(dataInput, ReadTrackers.UNLIMITED);
            if (namelessTag == null) namedTag = null;
            else namedTag = new NamedTag("", namelessTag.getNbtType(), namelessTag);
        } else {
            namedTag = formatType.getNbtIO().readNamed(dataInput, ReadTrackers.UNLIMITED);
        }
        if (bais.available() > 0 && readExtraData) {
            CompoundTag extraCompound = new CompoundTag(new LinkedHashMap<>());
            NamedTag extraRoot = new NamedTag("extra data", NbtType.COMPOUND, extraCompound);
            if (namedTag != null) extraCompound.add(namedTag.getName(), namedTag.getTag());
            namedTag = extraRoot;

            while (bais.available() > 0) {
                if (namelessRoot) {
                    INbtTag extra = formatType.getNbtIO().readUnnamed(dataInput, ReadTrackers.UNLIMITED);
                    if (extra == null) continue;
                    extraCompound.add(TagUtils.findUniqueName(extraCompound, ""), extra);
                } else {
                    NamedTag extra = formatType.getNbtIO().readNamed(dataInput, ReadTrackers.UNLIMITED);
                    if (extra == null) continue;
                    extraCompound.add(TagUtils.findUniqueName(extraCompound, extra.getName()), extra.getTag());
                }
            }
        }
        return new ReadResult(Optional.ofNullable(namedTag), customFormat, bais.available());
    }


    public record ReadResult(Optional<NamedTag> namedTag, ICustomFormat customFormat, int unreadBytes) {
    }

}
