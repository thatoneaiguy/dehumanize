package com.everest.hibiscus;

@FunctionalInterface
public interface Shader {
    /**
     * @param color     original ARGB color sampled from texture
     * @param distance  perpendicular distance from camera to wall
     * @param side      wall side (0 or 1)
     * @return new ARGB color
     */
    int shade(int color, double distance, int side);

    Shader DEFAULT = (color, distance, side) -> {
        int alpha = (color >>> 24);
        if (alpha == 0) return color;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;

        double att = 1.0 / (1.0 + distance * 0.12);
        if (att < 0) att = 0;
        if (att > 1) att = 1;

        double sideFactor = (side == 1) ? 0.75 : 1.0;

        double factor = att * sideFactor;

        r = (int) (r * factor);
        g = (int) (g * factor);
        b = (int) (b * factor);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return (alpha << 24) | (r << 16) | (g << 8) | b;
    };
}
