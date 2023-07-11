package net.lenni0451.imnbt.utils.nbt;

import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.popups.MessagePopup;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.*;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.lenni0451.imnbt.ui.types.Popup.PopupCallback.close;

public class TagTransformer {

    public static NbtType[] BYTE_TRANSFORMS = new NbtType[]{NbtType.SHORT, NbtType.INT, NbtType.LONG, NbtType.FLOAT, NbtType.DOUBLE};
    public static NbtType[] SHORT_TRANSFORMS = new NbtType[]{NbtType.BYTE, NbtType.INT, NbtType.LONG, NbtType.FLOAT, NbtType.DOUBLE};
    public static NbtType[] INT_TRANSFORMS = new NbtType[]{NbtType.BYTE, NbtType.SHORT, NbtType.LONG, NbtType.FLOAT, NbtType.DOUBLE};
    public static NbtType[] LONG_TRANSFORMS = new NbtType[]{NbtType.BYTE, NbtType.SHORT, NbtType.INT, NbtType.FLOAT, NbtType.DOUBLE};
    public static NbtType[] FLOAT_TRANSFORMS = new NbtType[]{NbtType.BYTE, NbtType.SHORT, NbtType.INT, NbtType.LONG, NbtType.DOUBLE};
    public static NbtType[] DOUBLE_TRANSFORMS = new NbtType[]{NbtType.BYTE, NbtType.SHORT, NbtType.INT, NbtType.LONG, NbtType.FLOAT};
    public static NbtType[] BYTE_ARRAY_TRANSFORMS = new NbtType[]{NbtType.LIST, NbtType.INT_ARRAY, NbtType.LONG_ARRAY};
    public static NbtType[] STRING_TRANSFORMS = new NbtType[]{};
    public static NbtType[] LIST_TRANSFORMS = new NbtType[]{NbtType.COMPOUND};
    public static NbtType[] COMPOUND_TRANSFORMS = new NbtType[]{NbtType.LIST};
    public static NbtType[] INT_ARRAY_TRANSFORMS = new NbtType[]{NbtType.LIST, NbtType.BYTE_ARRAY, NbtType.LONG_ARRAY};
    public static NbtType[] LONG_ARRAY_TRANSFORMS = new NbtType[]{NbtType.LIST, NbtType.BYTE_ARRAY, NbtType.INT_ARRAY};

    private static void invalidType(final NbtType type) {
        throw new IllegalArgumentException("Unable to convert tag to " + type.name());
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final ByteTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case SHORT -> transformListener.accept(name, new ShortTag(tag.shortValue()));
                case INT -> transformListener.accept(name, new IntTag(tag.intValue()));
                case LONG -> transformListener.accept(name, new LongTag(tag.longValue()));
                case FLOAT -> transformListener.accept(name, new FloatTag(tag.floatValue()));
                case DOUBLE -> transformListener.accept(name, new DoubleTag(tag.doubleValue()));
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final ShortTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE -> transformListener.accept(name, new ByteTag(tag.byteValue()));
                case INT -> transformListener.accept(name, new IntTag(tag.intValue()));
                case LONG -> transformListener.accept(name, new LongTag(tag.longValue()));
                case FLOAT -> transformListener.accept(name, new FloatTag(tag.floatValue()));
                case DOUBLE -> transformListener.accept(name, new DoubleTag(tag.doubleValue()));
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final IntTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE -> transformListener.accept(name, new ByteTag(tag.byteValue()));
                case SHORT -> transformListener.accept(name, new ShortTag(tag.shortValue()));
                case LONG -> transformListener.accept(name, new LongTag(tag.longValue()));
                case FLOAT -> transformListener.accept(name, new FloatTag(tag.floatValue()));
                case DOUBLE -> transformListener.accept(name, new DoubleTag(tag.doubleValue()));
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final LongTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE -> transformListener.accept(name, new ByteTag(tag.byteValue()));
                case SHORT -> transformListener.accept(name, new ShortTag(tag.shortValue()));
                case INT -> transformListener.accept(name, new IntTag(tag.intValue()));
                case FLOAT -> transformListener.accept(name, new FloatTag(tag.floatValue()));
                case DOUBLE -> transformListener.accept(name, new DoubleTag(tag.doubleValue()));
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final FloatTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE -> transformListener.accept(name, new ByteTag(tag.byteValue()));
                case SHORT -> transformListener.accept(name, new ShortTag(tag.shortValue()));
                case INT -> transformListener.accept(name, new IntTag(tag.intValue()));
                case LONG -> transformListener.accept(name, new LongTag(tag.longValue()));
                case DOUBLE -> transformListener.accept(name, new DoubleTag(tag.doubleValue()));
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final DoubleTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE -> transformListener.accept(name, new ByteTag(tag.byteValue()));
                case SHORT -> transformListener.accept(name, new ShortTag(tag.shortValue()));
                case INT -> transformListener.accept(name, new IntTag(tag.intValue()));
                case LONG -> transformListener.accept(name, new LongTag(tag.longValue()));
                case FLOAT -> transformListener.accept(name, new FloatTag(tag.floatValue()));
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final ByteArrayTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case LIST -> {
                    ListTag<ByteTag> byteList = new ListTag<>();
                    for (byte b : tag.getValue()) byteList.add(new ByteTag(b));
                    transformListener.accept(name, byteList);
                }
                case INT_ARRAY -> {
                    int[] ints = new int[tag.getValue().length];
                    for (int i = 0; i < tag.getValue().length; i++) ints[i] = tag.getValue()[i];
                    transformListener.accept(name, new IntArrayTag(ints));
                }
                case LONG_ARRAY -> {
                    long[] longs = new long[tag.getValue().length];
                    for (int i = 0; i < tag.getValue().length; i++) longs[i] = tag.getValue()[i];
                    transformListener.accept(name, new LongArrayTag(longs));
                }
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final StringTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final ListTag<?> tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case COMPOUND -> {
                    CompoundTag compound = new CompoundTag();
                    for (int i = 0; i < tag.size(); i++) compound.add(String.valueOf(i), tag.get(i));
                    transformListener.accept(name, compound);
                }
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final CompoundTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case LIST -> {
                    ListTag<INbtTag> list = new ListTag<>();
                    for (Map.Entry<String, INbtTag> entry : tag) {
                        if (!list.canAdd(entry.getValue())) {
                            drawer.openPopup(new MessagePopup("Error", "Lists can only contain one tag type.", close(drawer)));
                            return;
                        }
                        list.add(entry.getValue());
                    }
                    transformListener.accept(name, list);
                }
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final IntArrayTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE_ARRAY -> {
                    byte[] bytes = new byte[tag.getValue().length];
                    for (int i = 0; i < tag.getValue().length; i++) bytes[i] = (byte) tag.getValue()[i];
                    transformListener.accept(name, new ByteArrayTag(bytes));
                }
                case LONG_ARRAY -> {
                    long[] longs = new long[tag.getValue().length];
                    for (int i = 0; i < tag.getValue().length; i++) longs[i] = tag.getValue()[i];
                    transformListener.accept(name, new LongArrayTag(longs));
                }
                default -> invalidType(type);
            }
        };
    }

    public static Consumer<NbtType> transform(final ImNbtDrawer drawer, final String name, final LongArrayTag tag, final BiConsumer<String, INbtTag> transformListener) {
        return type -> {
            switch (type) {
                case BYTE_ARRAY -> {
                    byte[] bytes = new byte[tag.getValue().length];
                    for (int i = 0; i < tag.getValue().length; i++) bytes[i] = (byte) tag.getValue()[i];
                    transformListener.accept(name, new ByteArrayTag(bytes));
                }
                case INT_ARRAY -> {
                    int[] ints = new int[tag.getValue().length];
                    for (int i = 0; i < tag.getValue().length; i++) ints[i] = (int) tag.getValue()[i];
                    transformListener.accept(name, new IntArrayTag(ints));
                }
                default -> invalidType(type);
            }
        };
    }

}
