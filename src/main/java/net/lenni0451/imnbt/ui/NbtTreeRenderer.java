package net.lenni0451.imnbt.ui;

import imgui.ImGui;
import imgui.ImVec2;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.nbt.*;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.ImageUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The renderer for the Nbt tree.
 */
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

    /**
     * Get the renderer for a tag type.
     *
     * @param type The type of the tag
     * @return The renderer for the tag type
     */
    public static TagRenderer getTagRenderer(@Nonnull final NbtType type) {
        return TAG_RENDERER.get(type);
    }

    /**
     * Render the tag tree.
     *
     * @param drawer            The drawer instance
     * @param nameEditConsumer  The listener for when the name of a tag is edited
     * @param transformListener The listener for when the tag is transformed
     * @param deleteListener    The listener for when the tag is deleted
     * @param colorProvider     The provider for the color of the tag
     * @param searchProvider    The provider for the search
     * @param openContextMenu   Whether the context menu should be opened
     * @param path              The path of the tag
     * @param name              The name of the tag
     * @param tag               The tag to render
     */
    public static void render(final ImNbtDrawer drawer, final Consumer<String> nameEditConsumer, final BiConsumer<String, INbtTag> transformListener, final Runnable deleteListener, final Function<String, Color> colorProvider, final SearchProvider searchProvider, final boolean openContextMenu, final String path, final String name, final INbtTag tag) {
        TagRenderer renderer = TAG_RENDERER.get(tag.getNbtType());
        if (renderer == null) ImGui.text("Missing renderer for tag type: " + tag.getNbtType().name());
        else renderer.render(drawer, nameEditConsumer, transformListener, deleteListener, colorProvider, searchProvider, openContextMenu, path, name, tag);
    }

    /**
     * Render the icon for a tag type.<br>
     * The size is based on the font size.
     *
     * @param drawer The drawer instance
     * @param xy     The position of the icon
     * @param type   The type of the tag
     */
    public static void renderIcon(final ImNbtDrawer drawer, final ImVec2 xy, final NbtType type) {
        int index = type.ordinal() - 1;
        int nbtTypes = NbtType.values().length - 1;
        int size = ImGui.getFontSize();
        ImGui.getWindowDrawList().addImage(
                drawer.getIconsTexture(),
                xy.x,
                xy.y,
                xy.x + size,
                xy.y + size,
                ImageUtils.calculateUV(16 * nbtTypes, 16 * index),
                0,
                ImageUtils.calculateUV(16 * nbtTypes, 16 * (index + 1)),
                1
        );
    }

}
