package net.lenni0451.imnbt.utils.nbt.diff;

import net.lenni0451.imnbt.utils.Color;

import javax.annotation.Nullable;

public enum DiffType {

    ADDED(new Color(0, 150, 0)),
    REMOVED(new Color(150, 0, 0)),
    TYPE_CHANGED(new Color(150, 150, 0)),
    VALUE_CHANGED(new Color(0, 150, 150)),
    CHILD_CHANGED(new Color(200, 0, 200)),
    UNCHANGED(null);


    private final Color color;

    DiffType(final Color color) {
        this.color = color;
    }

    @Nullable
    public Color getColor() {
        return this.color;
    }

}
