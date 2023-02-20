package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.LongArrayTag;
import net.lenni0451.mcstructs.nbt.tags.LongTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class LongArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        LongArrayTag longArrayTag = (LongArrayTag) tag;
        this.renderBranch(name, "(" + longArrayTag.getLength() + ")", path, () -> {
            ContextMenu.start().singleType(NbtType.LONG, (newName, newTag) -> {
                longArrayTag.add(((LongTag) newTag).getValue());
            }).edit(name, longArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).render();
        }, () -> {
            int[] removed = new int[]{-1};
            for (int i = 0; i < longArrayTag.getLength(); i++) {
                final int fi = i;
                this.renderLeaf(i + ": " + this.format.format(longArrayTag.get(i)) + "##" + i, path, () -> {
                    ContextMenu.start().edit(String.valueOf(fi), new LongTag(longArrayTag.get(fi)), newName -> {
                        //This gets executed multiple frames after the user clicked save in the popup
                        try {
                            int newIndex = Integer.parseInt(newName);
                            if (newIndex < 0 || newIndex >= longArrayTag.getLength() || newIndex == fi) return;
                            long val = longArrayTag.get(fi);
                            long[] newValue = ArrayUtils.remove(longArrayTag.getValue(), fi);
                            newValue = ArrayUtils.insert(newValue, newIndex, val);
                            longArrayTag.setValue(newValue);
                        } catch (Throwable ignored) {
                        }
                    }, newTag -> longArrayTag.set(fi, newTag.getValue())).delete(() -> removed[0] = fi).render();
                });
            }
            if (removed[0] != -1) longArrayTag.setValue(ArrayUtils.remove(longArrayTag.getValue(), removed[0]));
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
