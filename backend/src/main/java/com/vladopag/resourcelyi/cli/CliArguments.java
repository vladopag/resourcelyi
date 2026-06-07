package com.vladopag.resourcelyi.cli;

public final class CliArguments {

    private CliArguments() {}

    public static boolean isCliMode(String... args) {
        return hasFlag(args, "--cli");
    }

    public static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    public static int parseInterval(String... args) {
        for (int i = 0; i < args.length; i++) {
            if ("--interval".equals(args[i]) || "-i".equals(args[i])) {
                if (i + 1 < args.length) {
                    return Integer.parseInt(args[i + 1]);
                }
            }
            if (args[i].startsWith("--interval=")) {
                return Integer.parseInt(args[i].substring("--interval=".length()));
            }
        }
        return 1;
    }
}
