package com.vladopag.resourcelyi.cli;

public final class TerminalRenderer {

    private static final String ENTER_ALT_SCREEN = "\033[?1049h";
    private static final String EXIT_ALT_SCREEN = "\033[?1049l";
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private static boolean active;

    private TerminalRenderer() {}

    public static boolean isInteractive() {
        return System.console() != null;
    }

    public static void enter() {
        if (isInteractive()) {
            System.out.print(ENTER_ALT_SCREEN);
            active = true;
        }
    }

    public static void render(String content) {
        if (isInteractive()) {
            System.out.print(CLEAR_SCREEN);
        }
        System.out.print(content);
        System.out.flush();
    }

    public static void exit() {
        if (active) {
            System.out.print(EXIT_ALT_SCREEN);
            active = false;
        }
    }
}
