package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.net.URL;

public class GameCompleteScreen extends JPanel {
    private final Runnable onReturnToMenu;
    private final Timer timer;
    private double tick = 0;

    private final ImageIcon background = loadIcon("Underworld_background_2.gif");
    private final ImageIcon header = loadIcon("credits_header.png");
    private final ImageIcon ghost = loadIcon("ghost.png");
    private final ImageIcon menuBtnNormal = loadIcon("button_menu.png");
    private final ImageButton backButton = new ImageButton("button_menu.png", "button_menu_pressed.png");

    public GameCompleteScreen(Runnable onReturnToMenu) {
        this.onReturnToMenu = onReturnToMenu;
        setLayout(null);
        setOpaque(true);
        setFocusable(true);
        backButton.addActionListener(e -> this.onReturnToMenu.run());
        add(backButton);

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
        int bw = clamp((int)(w * 0.32), 340, 465);
        int bh = (int)(bw * (126.0 / 446.0));
        int x = (w - bw) / 2;
        int y = h - bh - clamp((int)(h * 0.055), 34, 62);
        backButton.setBounds(x, y, bw, bh);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        pixel(g2);
        int w = getWidth();
        int h = getHeight();

        drawCover(g2, background, w, h, 0.53, 0.52);
        g2.setColor(new Color(0, 0, 0, 105));
        g2.fillRect(0, 0, w, h);
        drawEmbers(g2, w, h);
        drawPanel(g2, w, h);
        drawCredits(g2, w, h);
        drawGhost(g2, w, h);
        drawVignette(g2, w, h);
        g2.dispose();
    }

    private Rectangle panelRect(int w, int h) {
        int panelW = clamp((int)(w * 0.53), 680, 845);
        int panelH = clamp((int)(h * 0.72), 540, 690);
        int x = (w - panelW) / 2;
        int y = clamp((int)(h * 0.035), 26, 42);
        return new Rectangle(x, y, panelW, panelH);
    }

    private void drawPanel(Graphics2D g2, int w, int h) {
        Rectangle p = panelRect(w, h);
        drawGlow(g2, p.x + p.width / 2, p.y + p.height, p.width / 2, new Color(255, 65, 0, 30));

        g2.setColor(new Color(0, 0, 0, 135));
        g2.fillRect(p.x + 12, p.y + 12, p.width, p.height);
        g2.setColor(new Color(10, 7, 12, 228));
        g2.fillRect(p.x, p.y, p.width, p.height);

        g2.setColor(new Color(64, 47, 62));
        g2.drawRect(p.x, p.y, p.width, p.height);
        g2.setColor(new Color(247, 82, 16));
        g2.drawRect(p.x + 8, p.y + 8, p.width - 16, p.height - 16);
        g2.setColor(new Color(92, 45, 88));
        g2.drawRect(p.x + 15, p.y + 15, p.width - 30, p.height - 30);
        g2.setColor(new Color(33, 22, 34));
        g2.drawRect(p.x + 24, p.y + 24, p.width - 48, p.height - 48);

        if (header != null) {
            int hw = clamp((int)(p.width * 0.78), 560, 700);
            int hh = (int)(hw * (header.getIconHeight() / (double)header.getIconWidth()));
            int hx = p.x + (p.width - hw) / 2;
            int hy = p.y + 12;
            g2.drawImage(header.getImage(), hx, hy, hw, hh, this);
        } else {
            drawStoneHeader(g2, p.x + 70, p.y + 20, p.width - 140, 90, "GAME COMPLETE");
        }

        drawChains(g2, p);
    }

    private void drawCredits(Graphics2D g2, int w, int h) {
        Rectangle p = panelRect(w, h);
        int cx = p.x + p.width / 2;
        int y = p.y + clamp((int)(p.height * 0.21), 128, 158);

        drawPixelText(g2, "- CREDITS -", cx, y, 38, true, new Color(255, 133, 22));
        y += 58;

        drawDivider(g2, cx, y - 16, 300);
        drawPixelText(g2, "DEVELOPERS", cx, y + 8, 24, true, new Color(235, 219, 206));
        y += 58;

        String[] devs = {
                "Docena, Shakira Marie",
                "Geverola, Flint Harvey",
                "Cañeda, Luke Brynson",
                "Alegam, Christian"
        };
        for (String d : devs) {
            drawPixelText(g2, d, cx, y, 23, false, new Color(255, 151, 33));
            y += 34;
        }

        y += 18;
        drawDivider(g2, cx, y - 15, 300);
        drawPixelText(g2, "SPECIAL THANKS", cx, y + 10, 25, true, new Color(235, 219, 206));
        y += 66;
        drawPixelText(g2, "Sir Chowchow", cx, y, 24, false, new Color(255, 126, 20));

        y += 62;
        drawDivider(g2, cx, y - 22, 300);
        drawPixelText(g2, "A FINAL REQUIREMENT", cx, y, 22, true, new Color(235, 219, 206));
        y += 34;
        drawPixelText(g2, "FOR OBJECT-ORIENTED PROGRAMMING 2", cx, y, 22, true, new Color(235, 219, 206));

//        y += 62;
//        drawDivider(g2, cx, y - 22, 300);
//        drawPixelText(g2, "TO GOD BE THE GLORY", cx, y, 26, true, new Color(255, 112, 15));
    }

    private void drawPixelText(Graphics2D g2, String text, int centerX, int baselineY, int size, boolean bold, Color color) {
        g2.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, size));
        FontMetrics fm = g2.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        g2.setColor(new Color(0, 0, 0, 210));
        g2.drawString(text, x + 3, baselineY + 3);
        g2.setColor(new Color(80, 30, 18, 120));
        g2.drawString(text, x + 1, baselineY + 1);
        g2.setColor(color);
        g2.drawString(text, x, baselineY);
    }

    private void drawDivider(Graphics2D g2, int cx, int y, int width) {
        g2.setColor(new Color(83, 34, 86));
        g2.drawLine(cx - width / 2, y, cx - 28, y);
        g2.drawLine(cx + 28, y, cx + width / 2, y);
        g2.setColor(new Color(156, 76, 160));
        g2.fillRect(cx - 5, y - 5, 10, 10);
        g2.setColor(new Color(40, 18, 44));
        g2.drawRect(cx - 7, y - 7, 14, 14);
    }

    private void drawChains(Graphics2D g2, Rectangle p) {
        g2.setStroke(new BasicStroke(2));
        for (int chainX : new int[]{p.x + 50, p.x + p.width - 64}) {
            for (int cy = p.y + 124; cy < p.y + p.height - 82; cy += 21) {
                g2.setColor(new Color(28, 26, 34));
                g2.fillRoundRect(chainX, cy, 13, 18, 6, 6);
                g2.setColor(new Color(61, 49, 62));
                g2.drawRoundRect(chainX, cy, 13, 18, 6, 6);
            }
        }
    }

    private void drawGhost(Graphics2D g2, int w, int h) {
        if (ghost == null) return;
        Rectangle p = panelRect(w, h);
        int gh = clamp((int)(h * 0.16), 100, 145);
        int gw = (int)(gh * (ghost.getIconWidth() / (double)ghost.getIconHeight()));
        int x = p.x + p.width - gw - 62;
        int y = p.y + p.height / 2 - 18 + (int)(Math.sin(tick + 1.5) * 6);
        g2.drawImage(ghost.getImage(), x, y, gw, gh, this);
    }

    private void drawStoneHeader(Graphics2D g2, int x, int y, int w, int h, String text) {
        Polygon p = new Polygon(
                new int[]{x, x + 30, x + w - 30, x + w, x + w - 30, x + 30},
                new int[]{y + h/2, y, y, y + h/2, y + h, y + h}, 6);
        g2.setColor(new Color(38, 27, 34));
        g2.fillPolygon(p);
        g2.setColor(new Color(130, 77, 77));
        g2.drawPolygon(p);
        drawPixelText(g2, text, x + w/2, y + h/2 + 15, 40, true, new Color(255, 139, 22));
    }

    private void drawEmbers(Graphics2D g2, int w, int h) {
        for (int i = 0; i < 52; i++) {
            int x = (int)((i * 157 + tick * 28) % (w + 120)) - 60;
            int y = (int)(h * .73) + ((i * 43) % Math.max(50, h / 4));
            int s = 2 + (i % 3);
            g2.setColor(new Color(255, 86, 10, 42 + (i % 4) * 18));
            g2.fillRect(x, y, s, s);
        }
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

    private void drawVignette(Graphics2D g2, int w, int h) {
        RadialGradientPaint rg = new RadialGradientPaint(
                new Point2D.Float(w * .5f, h * .48f), Math.max(w, h) * .68f,
                new float[]{0f, .68f, 1f},
                new Color[]{new Color(0,0,0,0), new Color(0,0,0,76), new Color(0,0,0,225)});
        g2.setPaint(rg);
        g2.fillRect(0, 0, w, h);
    }

    private void drawGlow(Graphics2D g2, int cx, int cy, int radius, Color color) {
        RadialGradientPaint glow = new RadialGradientPaint(new Point2D.Float(cx, cy), radius,
                new float[]{0f, .52f, 1f},
                new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/3), new Color(0,0,0,0)});
        g2.setPaint(glow);
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
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
            if (icon != null && icon.getIconWidth() > 0) g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
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
        URL url = GameCompleteScreen.class.getResource("/assets/" + name);
        if (url != null) return new ImageIcon(url);
        File f = new File("src/main/resources/assets/" + name);
        return f.exists() ? new ImageIcon(f.getPath()) : null;
    }
}
