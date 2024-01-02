package net.lenni0451.imnbt.application;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class KeyboardHelper {

    public static final int KEY_S;
    public static final int KEY_Y;
    public static final int KEY_Z;
    public static final int KEY_CTRL_LEFT = GLFW.GLFW_KEY_LEFT_CONTROL;
    public static final int KEY_CTRL_RIGHT = GLFW.GLFW_KEY_RIGHT_CONTROL;

    static {
        KEY_S = guessKey("S", GLFW.GLFW_KEY_S);
        KEY_Y = guessKey("Y", GLFW.GLFW_KEY_Y, GLFW.GLFW_KEY_Z);
        KEY_Z = guessKey("Z", GLFW.GLFW_KEY_Y, GLFW.GLFW_KEY_Z);
    }

    /**
     * Guess the key from the name because of keyboard layouts.
     *
     * @param keyName The name of the key
     * @param keys    The keys to guess from
     * @return The guessed key or -1 if not found
     */
    private static int guessKey(final String keyName, final int... keys) {
        for (int key : keys) {
            String name = GLFW.glfwGetKeyName(key, -1);
            if (name != null && name.equalsIgnoreCase(keyName)) return key;
        }
        return -1;
    }

    public static boolean isCtrlPressed() {
        return ImGui.isKeyDown(KeyboardHelper.KEY_CTRL_LEFT) || ImGui.isKeyDown(KeyboardHelper.KEY_CTRL_RIGHT);
    }

}
