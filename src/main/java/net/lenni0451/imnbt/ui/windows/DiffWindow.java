package net.lenni0451.imnbt.ui.windows;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.NbtTreeRenderer;
import net.lenni0451.imnbt.ui.SearchProvider;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.utils.Color;
import net.lenni0451.imnbt.utils.StringUtils;
import net.lenni0451.imnbt.utils.nbt.diff.DiffMap;
import net.lenni0451.imnbt.utils.nbt.diff.DiffType;
import net.lenni0451.imnbt.utils.nbt.diff.Differ;
import net.lenni0451.mcstructs.nbt.INbtTag;

/**
 * The window to display the diff of two tags.
 */
public class DiffWindow extends Window {

    private final SearchProvider searchProvider = new SearchProvider(this.drawer); //Unused here as there is no search (yet?)
    private INbtTag left;
    private INbtTag right;
    private DiffMap diffMap;

    public DiffWindow(ImNbtDrawer drawer) {
        super(drawer);
    }

    public void diff(final INbtTag left, final INbtTag right) {
        this.left = left;
        this.right = right;

        this.diffMap = Differ.diff(this.left, this.right);
    }

    @Override
    public void render() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.menuItem("Close")) {
                this.hide();
            }
            ImGui.separator();
            boolean first = true;
            for (DiffType value : DiffType.values()) {
                if (first) first = false;
                else ImGui.sameLine();

                Color color = value.getColor();
                if (color != null) ImGui.pushStyleColor(ImGuiCol.Text, color.getABGR());
                ImGui.text(StringUtils.format(value));
                if (color != null) ImGui.popStyleColor();
            }

            ImGui.endMenuBar();
        }

        if (ImGui.beginTable("DiffTable", 2)) {
            ImGui.tableSetupColumn("Left");
            ImGui.tableSetupColumn("Right");

            ImGui.tableNextColumn();
            NbtTreeRenderer.render(drawer, null, null, null, p -> this.diffMap.getLeft(p).getColor(), this.searchProvider, false, "", "", this.left);
            ImGui.tableNextColumn();
            NbtTreeRenderer.render(drawer, null, null, null, p -> this.diffMap.getRight(p).getColor(), this.searchProvider, false, "", "", this.right);

            ImGui.endTable();
        }
    }

}
