package net.lenni0451.imnbt.types.formats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface ICustomFormat {

    /**
     * Detect if the data is in this custom format.<br>
     * The data may have any length, checking the length is recommended.
     *
     * @param data The data to check
     * @return If the data is in this custom format
     */
    boolean detect(final byte[] data);

    /**
     * Read the custom data from the input.<br>
     * The input is in the correct endianess and decompressed if needed.
     *
     * @param in The input to read from
     * @return The new input to read the nbt data from
     * @throws IOException If an I/O error occurs
     */
    DataInput read(final DataInput in) throws IOException;

    /**
     * Write the custom data to the output.<br>
     * The output is in the correct endianess and compressed if needed.<br>
     * It is required to write the data as it is the serialized nbt data.
     *
     * @param out  The output to write to
     * @param data The data to write
     * @throws IOException If an I/O error occurs
     */
    void write(final DataOutput out, final byte[] data) throws IOException;

}
