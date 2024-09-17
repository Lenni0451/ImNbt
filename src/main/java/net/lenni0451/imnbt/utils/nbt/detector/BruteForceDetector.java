package net.lenni0451.imnbt.utils.nbt.detector;

import net.lenni0451.imnbt.types.CompressionType;
import net.lenni0451.imnbt.types.CustomFormatType;
import net.lenni0451.imnbt.types.EndianType;
import net.lenni0451.imnbt.types.FormatType;
import net.lenni0451.imnbt.utils.nbt.NbtReader;
import net.lenni0451.imnbt.utils.nbt.ReadTrackers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A format detector that tries to parse the file with all possible formats and uses the first one that doesn't throw an exception.
 */
public class BruteForceDetector {

    private final byte[] data;
    private final List<Result> results;

    public BruteForceDetector(final byte[] data) {
        this.data = data;
        this.results = new ArrayList<>();
    }

    public Optional<Result> run() {
        for (CompressionType compressionType : CompressionType.values()) {
            for (EndianType endianType : EndianType.values()) {
                for (FormatType formatType : FormatType.values()) {
                    for (CustomFormatType customFormatType : CustomFormatType.values()) {
                        for (boolean namelessRoot : new boolean[]{false, true}) {
                            this.tryParse(compressionType, endianType, formatType, customFormatType, namelessRoot);
                            if (Thread.currentThread().isInterrupted()) return Optional.empty();
                        }
                    }
                }
            }
        }
        this.results.sort(Comparator.comparingInt(o -> o.unreadBytes));
        if (this.results.isEmpty()) return Optional.empty();
        return Optional.of(this.results.get(0));
    }

    private void tryParse(final CompressionType compressionType, final EndianType endianType, final FormatType formatType, final CustomFormatType customFormatType, final boolean namelessRoot) {
        try {
            NbtReader.ReadResult result = NbtReader.read(this.data, ReadTrackers.STRICT, compressionType, endianType, formatType, customFormatType, namelessRoot, false);
            this.results.add(new Result(compressionType, endianType, formatType, customFormatType, namelessRoot, result.unreadBytes()));
        } catch (Throwable ignored) {
        }
    }


    public record Result(CompressionType compressionType, EndianType endianType, FormatType formatType, CustomFormatType customFormatType, boolean namelessRoot, int unreadBytes) {
    }

}
