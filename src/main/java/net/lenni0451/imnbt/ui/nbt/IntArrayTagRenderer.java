package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.IntArrayTag;
import net.lenni0451.mcstructs.nbt.tags.IntTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.lenni0451.imnbt.utils.NbtPath.get;

public class IntArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, Function<String, Color> colorProvider, String path, String name, @Nonnull INbtTag tag) {
        IntArrayTag intArrayTag = (IntArrayTag) tag;
        this.renderBranch(name, "(" + intArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(10);
            ContextMenu.start().singleType(NbtType.INT, (newName, newTag) -> {
                int index = -1;
                try {
                    int newIndex = Integer.parseInt(newName);
                    if (newIndex >= 0 && newIndex <= intArrayTag.getLength()) index = newIndex;
                } catch (Throwable ignored) {
                }
                if (index == -1) intArrayTag.add(((IntTag) newTag).getValue());
                else intArrayTag.setValue(ArrayUtils.insert(intArrayTag.getValue(), index, ((IntTag) newTag).getValue()));
            }).edit(name, intArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
        }, () -> {
            int[] removed = new int[]{-1};
            for (int i = 0; i < intArrayTag.getLength(); i++) {
                final int fi = i;
                this.renderLeaf(String.valueOf(i), ": " + this.format.format(intArrayTag.get(i)), get(path, i), () -> {
                    this.renderIcon(2);
                    ContextMenu.start().edit(String.valueOf(fi), new IntTag(intArrayTag.get(fi)), newName -> {
                        //This gets executed multiple frames after the user clicked save in the popup
                        try {
                            int newIndex = Integer.parseInt(newName);
                            if (newIndex < 0 || newIndex >= intArrayTag.getLength() || newIndex == fi) return;
                            int val = intArrayTag.get(fi);
                            int[] newValue = ArrayUtils.remove(intArrayTag.getValue(), fi);
                            newValue = ArrayUtils.insert(newValue, newIndex, val);
                            intArrayTag.setValue(newValue);
                        } catch (Throwable ignored) {
                        }
                    }, newTag -> intArrayTag.set(fi, newTag.getValue())).delete(() -> removed[0] = fi).render();
                }, colorProvider);
            }
            if (removed[0] != -1) intArrayTag.setValue(ArrayUtils.remove(intArrayTag.getValue(), removed[0]));
        }, colorProvider);
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
