package net.lenni0451.imnbt.config;

import java.util.HashMap;
import java.util.Map;

/**
 * A default config implementation which stores the values in a map.<br>
 * Options are not preserved between sessions.
 */
public class SessionConfig implements ImNbtConfig {

    private final Map<String, Object> config = new HashMap<>();

    @Override
    public boolean getBoolean(String key, boolean def) {
        Object value = this.config.get(key);
        if (!(value instanceof Boolean)) return def;
        return (Boolean) value;
    }

    @Override
    public void setBoolean(String key, boolean value) {
        this.config.put(key, value);
    }

}
