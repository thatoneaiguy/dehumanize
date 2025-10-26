package com.everest.hibiscus;

import com.everest.config.EngineConfig;

public class Frustum {
    private final double halfFov;
    private final double margin;

    public Frustum() {
        this.halfFov = Math.toRadians(EngineConfig.FOV / 2.0);
        this.margin = Math.toRadians(5);
    }

    public boolean isInView(double x, double y, double camX, double camY, double dirX, double dirY) {
        double[][] corners = {
                {x - 0.5, y - 0.5},
                {x + 0.5, y - 0.5},
                {x - 0.5, y + 0.5},
                {x + 0.5, y + 0.5}
        };

        for (double[] corner : corners) {
            double dx = corner[0] - camX;
            double dy = corner[1] - camY;
            double dot = dx * dirX + dy * dirY;
            if (dot <= 0) continue;

            double angleToPoint = Math.atan2(dy, dx);
            double camAngle = Math.atan2(dirY, dirX);
            double delta = normalizeAngle(angleToPoint - camAngle);

            if (Math.abs(delta) <= halfFov + margin) return true;
        }

        return false;
    }

    private double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
