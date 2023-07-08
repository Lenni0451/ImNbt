package net.lenni0451.imnbt.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A class holding all licenses for the libraries used in ImNbt.
 */
public class Licenses {

    public static final String GUAVA;
    public static final String IMGUI;
    public static final String IMGUI_JAVA;
    public static final String IMNBT;
    public static final String LWJGL;
    public static final String MCSTRUCTS;
    public static final String MCSTRUCTS_BEDROCK;
    public static final String OPENSANS;
    public static final String PNGDECODER;

    static {
        GUAVA = read("guava");
        IMGUI = read("imgui");
        IMGUI_JAVA = read("imgui-java");
        IMNBT = read("imnbt");
        LWJGL = read("lwjgl");
        MCSTRUCTS = read("mcstructs");
        MCSTRUCTS_BEDROCK = read("mcstructs-bedrock");
        OPENSANS = read("opensans");
        PNGDECODER = read("pngdecoder");
    }

    private static String read(final String name) {
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(Licenses.class.getClassLoader().getResourceAsStream("imnbt/licenses/" + name + ".txt")));
        br.lines().forEach(line -> out.append(line).append("\n"));
        return out.toString();
    }

}
