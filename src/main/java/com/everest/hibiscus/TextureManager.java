package com.everest.hibiscus;

import com.everest.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class TextureManager {
    private static final Map<Identifier, Texture> textureCache = new HashMap<>();
    private static final String TEXTURE_PATH = "textures/";

    static {
        loadAllTextures();
    }

    private static void loadAllTextures() {
        try {
            Enumeration<URL> urls = TextureManager.class.getClassLoader().getResources(TEXTURE_PATH);
            List<String> names = new ArrayList<>();

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Path path;
                try {
                    path = Paths.get(url.toURI());
                    Files.list(path).forEach(p -> {
                        if (p.toString().endsWith(".png")) {
                            names.add(p.getFileName().toString().replace(".png",""));
                        }
                    });
                } catch (URISyntaxException | IOException e) {
                    System.err.println("[TextureManager] Could not auto-scan folder, fallback required");
                    names.addAll(List.of("wood","brick","bluestone","stone"));
                }
            }

            for (String name : names) {
                Identifier id = new Identifier("texture", name);
                try (InputStream is = TextureManager.class.getClassLoader().getResourceAsStream(TEXTURE_PATH + name + ".png")) {
                    if (is == null) continue;
                    Texture tex = new Texture(is);
                    textureCache.put(id, tex);
                    System.out.println("[TextureManager] Loaded " + id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Texture get(Identifier id) { return textureCache.get(id); }
    public static Set<Identifier> getLoadedIds() { return textureCache.keySet(); }
    public static void reload() {
        textureCache.clear();
        loadAllTextures();
    }
}
