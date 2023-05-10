package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.nbt.*;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NbtTreeRenderer {

    private static final Map<NbtType, TagRenderer> TAG_RENDERER = new EnumMap<>(NbtType.class);

    static {
        TAG_RENDERER.put(NbtType.BYTE, new ByteTagRenderer());
        TAG_RENDERER.put(NbtType.SHORT, new ShortTagRenderer());
        TAG_RENDERER.put(NbtType.INT, new IntTagRenderer());
        TAG_RENDERER.put(NbtType.LONG, new LongTagRenderer());
        TAG_RENDERER.put(NbtType.FLOAT, new FloatTagRenderer());
        TAG_RENDERER.put(NbtType.DOUBLE, new DoubleTagRenderer());
        TAG_RENDERER.put(NbtType.BYTE_ARRAY, new ByteArrayTagRenderer());
        TAG_RENDERER.put(NbtType.STRING, new StringTagRenderer());
        TAG_RENDERER.put(NbtType.LIST, new ListTagRenderer());
        TAG_RENDERER.put(NbtType.COMPOUND, new CompoundTagRenderer());
        TAG_RENDERER.put(NbtType.INT_ARRAY, new IntArrayTagRenderer());
        TAG_RENDERER.put(NbtType.LONG_ARRAY, new LongArrayTagRenderer());
    }

    public static TagRenderer getTagRenderer(@Nonnull final NbtType type) {
        return TAG_RENDERER.get(type);
    }

    public static void render(final ImNbtDrawer drawer, final Consumer<String> nameEditConsumer, final Runnable deleteListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path, final String name, final INbtTag tag) {
        TagRenderer renderer = TAG_RENDERER.get(tag.getNbtType());
        if (renderer == null) ImGui.text("Missing renderer for tag type: " + tag.getNbtType().name());
        else renderer.render(drawer, nameEditConsumer, deleteListener, colorProvider, searchProvider, openContextMenu, path, name, tag);
    }

}
