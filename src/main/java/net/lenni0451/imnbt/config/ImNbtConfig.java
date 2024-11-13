package net.lenni0451.imnbt.config;

/**
 * A configuration interface for ImNbt.<br>
 * Configurations don't need to be saved, but make the user experience better.
 */
public interface ImNbtConfig {

    String ADVANCED_FORMAT_DETECTION = "AdvancedFormatDetection";

    boolean getBoolean(final String key, final boolean def);

    void setBoolean(final String key, final boolean value);

    default void toggle(final String key, final boolean def) {
        this.setBoolean(key, !this.getBoolean(key, def));
    }

}
