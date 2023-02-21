package net.lenni0451.imnbt;

import imgui.app.Application;

public class Main {

    public static final String VERSION = "${version}";

    public static void main(String[] args) {
        Application.launch(new ImGuiImpl());
    }

}
