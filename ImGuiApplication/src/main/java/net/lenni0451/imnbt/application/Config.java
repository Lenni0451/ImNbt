package net.lenni0451.imnbt.application;

import net.lenni0451.imnbt.config.ImNbtConfig;

import java.util.prefs.Preferences;

public class Config implements ImNbtConfig {

    private Preferences preferences;

    public Config() {
        try {
            this.preferences = Preferences.userNodeForPackage(Config.class);
        } catch (Throwable t) {
            t.printStackTrace();
            this.preferences = null;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        if (this.preferences == null) return def;
        return this.preferences.getBoolean(key, def);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        if (this.preferences != null) {
            try {
                this.preferences.putBoolean(key, value);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
