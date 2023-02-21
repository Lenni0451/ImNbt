package net.lenni0451.imnbt.utils.imgui;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.annotation.Nullable;

public class FileDialogs {

    @Nullable
    public static String save(final String title) {
        MemoryStack memoryStack = MemoryStack.stackPush();
        PointerBuffer pointerBuffer = memoryStack.callocPointer(1);
        pointerBuffer.put(memoryStack.UTF8("*.*")).flip();
        String response = TinyFileDialogs.tinyfd_saveFileDialog(title, null, pointerBuffer, "Any File");
        memoryStack.pop();
        return response;
    }

    @Nullable
    public static String open(final String title) {
        MemoryStack memoryStack = MemoryStack.stackPush();
        PointerBuffer pointerBuffer = memoryStack.callocPointer(1);
        pointerBuffer.put(memoryStack.UTF8("*.*")).flip();
        String response = TinyFileDialogs.tinyfd_openFileDialog(title, null, pointerBuffer, "Any File", false);
        memoryStack.pop();
        return response;
    }

}
