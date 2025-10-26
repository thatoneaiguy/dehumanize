package com.everest.hibiscus;

import com.everest.config.EngineConfig;
import com.everest.util.Identifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Screen {
    private final int[][] map;
    private final int mapWidth, mapHeight, width, height;
    private final List<Identifier> textureIds = new ArrayList<>();
    private ShaderProgram shader;
    private Frustum frustum;

    public Screen(int[][] m, int mapW, int mapH, int w, int h) {
        this.map = m;
        this.mapWidth = mapW;
        this.mapHeight = mapH;
        this.width = w;
        this.height = h;

        textureIds.addAll(TextureManager.getLoadedIds());
        this.frustum = new Frustum();
    }

    public void attachShader(ShaderProgram shader) {
        this.shader = shader;
        System.out.println("[Screen] Shader attached: " + shader.getId());
    }

    public void setFrustum(Frustum frustum) {
        this.frustum = frustum;
    }

    public int[] update(Camera camera, int[] pixels) {
        if (shader != null) shader.use();

        for (int n = 0; n < pixels.length / 2; n++) pixels[n] = Color.DARK_GRAY.getRGB();
        for (int i = pixels.length / 2; i < pixels.length; i++) pixels[i] = Color.GRAY.getRGB();

        Identifier[] texArray = textureIds.toArray(new Identifier[0]);
        double halfFov = Math.toRadians(EngineConfig.FOV / 2.0);

        for (int x = 0; x < width; x++) {
            double cameraX = 2 * x / (double) width - 1;
            cameraX *= Math.tan(halfFov);

            double rayDirX = camera.xDir + camera.xPlane * cameraX;
            double rayDirY = camera.yDir + camera.yPlane * cameraX;

            int mapX = (int) camera.xPos;
            int mapY = (int) camera.yPos;

            double sideDistX, sideDistY;
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
            double perpWallDist;
            int stepX, stepY;
            boolean hit = false;
            int side = 0;

            if (rayDirX < 0) { stepX = -1; sideDistX = (camera.xPos - mapX) * deltaDistX; }
            else { stepX = 1; sideDistX = (mapX + 1.0 - camera.xPos) * deltaDistX; }

            if (rayDirY < 0) { stepY = -1; sideDistY = (camera.yPos - mapY) * deltaDistY; }
            else { stepY = 1; sideDistY = (mapY + 1.0 - camera.yPos) * deltaDistY; }

            while (!hit) {
                if (sideDistX < sideDistY) { sideDistX += deltaDistX; mapX += stepX; side = 0; }
                else { sideDistY += deltaDistY; mapY += stepY; side = 1; }

                if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) break;

                if (!frustum.isInView(mapX + 0.5, mapY + 0.5, camera.xPos, camera.yPos, camera.xDir, camera.yDir)) {
                    continue;
                }

                if (map[mapX][mapY] > 0) hit = true;
            }

            if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) continue;

            perpWallDist = (side == 0)
                    ? Math.abs((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX)
                    : Math.abs((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY);

            int lineHeight = (perpWallDist > 0) ? Math.abs((int) (height / perpWallDist)) : height;
            int drawStart = Math.max(-lineHeight / 2 + height / 2, 0);
            int drawEnd = Math.min(lineHeight / 2 + height / 2, height - 1);

            int texNum = map[mapX][mapY] - 1;
            if (texNum < 0 || texNum >= texArray.length) continue;

            Texture tex = TextureManager.get(texArray[texNum]);
            if (tex == null) continue;

            double wallX = (side == 1)
                    ? (camera.xPos + ((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY) * rayDirX)
                    : (camera.yPos + ((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);
            wallX -= Math.floor(wallX);

            int texX = (int) (wallX * tex.SIZE);
            if (side == 0 && rayDirX > 0) texX = tex.SIZE - texX - 1;
            if (side == 1 && rayDirY < 0) texX = tex.SIZE - texX - 1;

            for (int y = drawStart; y < drawEnd; y++) {
                int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color = tex.pixels[texX + texY * tex.SIZE];
                if (side == 1) color = (color >> 1) & 8355711;
                pixels[x + y * width] = color;
            }
        }

        return pixels;
    }
}
