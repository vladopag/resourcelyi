package com.vladopag.resourcelyi.support;

public final class DurationFormatter {

    private DurationFormatter() {}

    public static String formatSeconds(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        }
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        }
        return String.format("%dm %ds", minutes, seconds);
    }
}
