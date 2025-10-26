package com.everest;

import com.everest.config.EngineConfig;
import com.everest.hibiscus.*;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private int[] pixels;

    private Camera camera;
    private Screen screen;

    // Example map
    public static int[][] map = {
            {1,1,1,1,1,1,1,1,2,2,2,2,2,2,2},
            {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
            {1,0,3,3,3,3,3,0,0,0,0,0,0,0,2},
            {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
            {1,0,3,0,0,0,3,0,2,2,2,0,2,2,2},
            {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
            {1,0,3,3,0,3,3,0,2,0,0,0,0,0,2},
            {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
            {1,1,1,1,1,1,1,1,4,4,4,0,4,4,4},
            {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
            {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
            {1,0,0,0,0,0,1,4,0,3,3,3,3,0,4},
            {1,0,0,0,0,0,1,4,0,3,3,3,3,0,4},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {1,1,1,1,1,1,1,4,4,4,4,4,4,4,4}
    };

    public Game() {
        super("dehumanize");

        thread = new Thread(this);

        image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        camera = new Camera(4.5, 4.5, 1, 0, 0, -0.66);

        screen = new Screen(map, map[0].length, map.length, 640, 480);

        addKeyListener(camera);
        setSize(640, 480);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        setVisible(true);

        start();
    }

    private synchronized void start() {
        running = true;
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;
        requestFocus();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                // Update game logic
                screen.update(camera, pixels);
                camera.update(map);
                delta--;
            }

            render();
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
