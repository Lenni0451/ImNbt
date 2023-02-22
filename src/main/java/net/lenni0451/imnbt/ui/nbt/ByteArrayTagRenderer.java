package net.lenni0451.imnbt.ui.nbt;

import net.lenni0451.imnbt.ui.ContextMenu;
import net.lenni0451.imnbt.ui.types.TagRenderer;
import net.lenni0451.imnbt.utils.ArrayUtils;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.ByteArrayTag;
import net.lenni0451.mcstructs.nbt.tags.ByteTag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ByteArrayTagRenderer implements TagRenderer {

    private final DecimalFormat format = new DecimalFormat();

    @Override
    public void render(Consumer<String> nameEditConsumer, Runnable deleteListener, String path, String name, @Nonnull INbtTag tag) {
        ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
        this.renderBranch(name, "(" + byteArrayTag.getLength() + ")", path, () -> {
            this.renderIcon(6);
            ContextMenu.start().singleType(NbtType.BYTE, (newName, newTag) -> {
                int index = -1;
                try {
                    int newIndex = Integer.parseInt(newName);
                    if (newIndex >= 0 && newIndex <= byteArrayTag.getLength()) index = newIndex;
                } catch (Throwable ignored) {
                }
                if (index == -1) byteArrayTag.add(((ByteTag) newTag).getValue());
                else byteArrayTag.setValue(ArrayUtils.insert(byteArrayTag.getValue(), index, ((ByteTag) newTag).getValue()));
            }).edit(name, byteArrayTag, nameEditConsumer, t -> {}).delete(deleteListener).sNbtParser(() -> tag).render();
        }, () -> {
            int[] removed = new int[]{-1};
            for (int i = 0; i < byteArrayTag.getLength(); i++) {
                final int fi = i;
                this.renderLeaf(String.valueOf(i), ": " + this.format.format(byteArrayTag.get(i)) + "##" + i, path, () -> {
                    this.renderIcon(0);
                    ContextMenu.start().edit(String.valueOf(fi), new ByteTag(byteArrayTag.get(fi)), newName -> {
                        //This gets executed multiple frames after the user clicked save in the popup
                        try {
                            int newIndex = Integer.parseInt(newName);
                            if (newIndex < 0 || newIndex >= byteArrayTag.getLength() || newIndex == fi) return;
                            byte val = byteArrayTag.get(fi);
                            byte[] newValue = ArrayUtils.remove(byteArrayTag.getValue(), fi);
                            newValue = ArrayUtils.insert(newValue, newIndex, val);
                            byteArrayTag.setValue(newValue);
                        } catch (Throwable ignored) {
                        }
                    }, newTag -> byteArrayTag.set(fi, newTag.getValue())).delete(() -> removed[0] = fi).render();
                });
            }
            if (removed[0] != -1) byteArrayTag.setValue(ArrayUtils.remove(byteArrayTag.getValue(), removed[0]));
        });
    }

    @Override
    public void renderValueEditor(INbtTag tag) {
    }

}
