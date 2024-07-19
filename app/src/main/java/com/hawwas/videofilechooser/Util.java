package com.hawwas.videofilechooser;

public final class Util {

    public static String getSize(long size) {
        float kilo = size / 1024f;
        float mega = kilo / 1024f;
        float giga = mega / 1024f;
        if (giga > 1) {
            return String.format("%.2f GB", giga);
        } else if (mega > 1) {
            return String.format("%.2f MB", mega);
        } else {
            return String.format("%.2f KB", kilo);
        }}
}
