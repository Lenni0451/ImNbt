package net.lenni0451.imnbt.application;

import imgui.app.Application;
import org.lwjgl.system.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    private static Main instance;

    public static void main(String[] args) {
        if (isMacOS() && !hasStartOnFirstThread()) {
            restartWithStartOnFirstThread(args);
            return;
        }
        instance = new Main();
        instance.init();
    }

    public static Main getInstance() {
        return instance;
    }


    private final Config config = new Config();
    private final FontConfig fontConfig = new FontConfig();
    private final ImGuiImpl imGuiImpl = new ImGuiImpl(this.config, this.fontConfig);

    private void init() {
        if (isMacOS()) {
            Configuration.GLFW_CHECK_THREAD0.set(false);
        }
        Application.launch(this.imGuiImpl);
    }

    public ImGuiImpl getImGuiImpl() {
        return this.imGuiImpl;
    }

    public FontConfig getFontConfig() {
        return this.fontConfig;
    }

    private static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private static boolean hasStartOnFirstThread() {
        return "true".equals(System.getProperty("imnbt.startedOnFirstThread"));
    }

    private static void restartWithStartOnFirstThread(String[] args) {
        String javaHome = System.getProperty("java.home");
        String java = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        List<String> command = new ArrayList<>();
        command.add(java);
        command.add("-XstartOnFirstThread");
        command.add("-Dimnbt.startedOnFirstThread=true");
        command.add("-cp");
        command.add(classpath);
        command.add(Main.class.getName());
        Collections.addAll(command, args);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            System.exit(process.waitFor());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
