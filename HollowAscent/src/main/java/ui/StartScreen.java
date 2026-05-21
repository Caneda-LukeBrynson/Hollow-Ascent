package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.net.URL;

public class StartScreen extends JPanel {
    private final Runnable onStartGame;
    private final Timer timer;
    private double tick = 0;

    private final ImageIcon background = loadIcon("Underworld_background_3.png");
    private final ImageIcon logo = loadIcon("hollow_ascent_logo.png");
    private final ImageIcon platform = loadIcon("platform_large.png");
    private final ImageIcon grim = loadIcon("grim_reaper.gif");
    private final ImageButton startButton = new ImageButton("button_start.png", "button_start_pressed.png");

    public StartScreen(Runnable onStartGame) {
        this.onStartGame = onStartGame;
        setLayout(null);
        setOpaque(true);
        setFocusable(true);
        startButton.addActionListener(e -> this.onStartGame.run());
        add(startButton);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutButton(); }
        });

        timer = new Timer(16, e -> {
            tick += 0.035;
            layoutButton();
            repaint();
        });
        timer.start();
    }

    @Override public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
        layoutButton();
    }

    private void layoutButton() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        int bw = clamp((int)(w * 0.34), 360, 520);
        int bh = (int)(bw * (170.0 / 495.0));
        int x = (w - bw) / 2;
        int y = h - bh - clamp((int)(h * 0.065), 42, 76);
        startButton.setBounds(x, y, bw, bh);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        pixel(g2);
        int w = getWidth();
        int h = getHeight();

        drawCover(g2, background, w, h, 0.52, 0.52);
        drawAtmosphere(g2, w, h);
        drawLogo(g2, w, h);
        drawHeroPlatform(g2, w, h);
        drawEmbers(g2, w, h);
        drawVignette(g2, w, h);

        g2.dispose();
    }

    private void drawLogo(Graphics2D g2, int w, int h) {
        if (logo == null || logo.getIconWidth() <= 0) return;
        int lw = clamp((int)(w * 0.47), 500, 720);
        int lh = (int)(lw * (logo.getIconHeight() / (double)logo.getIconWidth()));
        int x = (w - lw) / 2;
        int y = clamp((int)(h * 0.045), 22, 55) + (int)(Math.sin(tick) * 7);
        drawGlow(g2, x + lw / 2, y + lh / 2, (int)(lw * .47), new Color(255, 70, 0, 42));
        g2.drawImage(logo.getImage(), x, y, lw, lh, this);
    }

    private void drawHeroPlatform(Graphics2D g2, int w, int h) {
        int pw = clamp((int)(w * 0.36), 420, 560);
        int ph = (int)(pw * (110.0 / 475.0));
        int px = (w - pw) / 2;
        int py = (int)(h * 0.61);

        if (platform != null) {
            drawGlow(g2, w/2, py + ph / 2, pw / 2, new Color(255, 78, 0, 40));
            g2.drawImage(platform.getImage(), px, py, pw, ph, this);
        }

        if (grim != null) {
            int gh = clamp((int)(h * 0.18), 96, 145);
            int gw = gh;
            int gx = (w - gw) / 2;
            int gy = py - gh + 24 + (int)(Math.sin(tick + 1.7) * 4);
            g2.drawImage(grim.getImage(), gx, gy, gw, gh, this);
        }
    }

    private void drawAtmosphere(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(6, 2, 6, 65));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,72), 0, h, new Color(0,0,0,18)));
        g2.fillRect(0, 0, w, h);
    }

    private void drawEmbers(Graphics2D g2, int w, int h) {
        for (int i = 0; i < 70; i++) {
            int x = (int)((i * 139 + tick * 36) % (w + 160)) - 80;
            int y = (int)(h * .68) + ((i * 53) % Math.max(70, h / 3));
            int s = 2 + (i % 3);
            int a = 45 + (i % 5) * 17;
            g2.setColor(new Color(255, 86, 13, a));
            g2.fillRect(x, y, s, s);
        }
    }

    private void drawVignette(Graphics2D g2, int w, int h) {
        RadialGradientPaint rg = new RadialGradientPaint(
                new Point2D.Float(w * .5f, h * .48f), Math.max(w, h) * .66f,
                new float[]{0f, .62f, 1f},
                new Color[]{new Color(0,0,0,0), new Color(0,0,0,72), new Color(0,0,0,235)});
        g2.setPaint(rg);
        g2.fillRect(0, 0, w, h);
    }

    private void drawGlow(Graphics2D g2, int cx, int cy, int radius, Color color) {
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(cx, cy), radius,
                new float[]{0f, .55f, 1f},
                new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/3), new Color(0,0,0,0)});
        g2.setPaint(glow);
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private void drawCover(Graphics2D g2, ImageIcon img, int w, int h, double focusX, double focusY) {
        if (img == null || img.getIconWidth() <= 0) {
            g2.setPaint(new GradientPaint(0, 0, new Color(18, 7, 13), 0, h, new Color(5, 2, 5)));
            g2.fillRect(0, 0, w, h);
            return;
        }
        int iw = img.getIconWidth();
        int ih = img.getIconHeight();
        double scale = Math.max(w / (double) iw, h / (double) ih);
        int nw = (int)Math.ceil(iw * scale);
        int nh = (int)Math.ceil(ih * scale);
        int x = (int)((w - nw) * focusX);
        int y = (int)((h - nh) * focusY);
        g2.drawImage(img.getImage(), x, y, nw, nh, this);
    }

    private static class ImageButton extends JButton {
        private final ImageIcon normal;
        private final ImageIcon pressed;
        ImageButton(String normalName, String pressedName) {
            normal = loadIcon(normalName);
            pressed = loadIcon(pressedName);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            pixel(g2);
            ImageIcon icon = (getModel().isPressed() || getModel().isRollover()) ? pressed : normal;
            if (icon != null && icon.getIconWidth() > 0) {
                g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
            g2.dispose();
        }
    }

    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    private static void pixel(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    private static ImageIcon loadIcon(String name) {
        String[] candidates = {
                "HollowAscent/assets/" + name,
                "assets/" + name,
                "../HollowAscent/assets/" + name,
                "../assets/" + name
        };
        for (String path : candidates) {
            File f = new File(path);
            if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        }
        URL url = StartScreen.class.getResource("/assets/" + name);
        if (url != null) return new ImageIcon(url);
        return null;
    }
}