package com.everest.hibiscus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Texture {
    public final int SIZE;
    public int[] pixels;

    public Texture(InputStream is) throws IOException {
        BufferedImage image = ImageIO.read(is);
        SIZE = image.getWidth();
        pixels = new int[SIZE * SIZE];
        image.getRGB(0, 0, SIZE, SIZE, pixels, 0, SIZE);
    }
}
