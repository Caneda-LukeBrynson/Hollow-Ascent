package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LevelCompleteScreen extends JPanel {

    private final Image backgroundGif;
    private float glowPulse = 0f;

    public LevelCompleteScreen() {
        this(null, null, null);
    }

    public LevelCompleteScreen(Runnable onRestart) {
        this(null, onRestart, null);
    }

    public LevelCompleteScreen(Runnable onNextLevel, Runnable onRestart, Runnable onBackToMenu) {
        setLayout(null);
        setOpaque(false);

        backgroundGif = new ImageIcon(
                getClass().getResource("/assets/Underworld_background_2.gif")
        ).getImage();

        Timer pulseTimer = new Timer(40, e -> {
            glowPulse += 0.05f;
            repaint();
        });
        pulseTimer.start();

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(8, 0, 8, 220));
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 90, 0));
                g2.drawRect(4, 4, w - 9, h - 9);
                g2.setColor(new Color(110, 40, 50));
                g2.drawRect(10, 10, w - 21, h - 21);
                g2.setColor(new Color(255, 150, 35));
                g2.drawLine(35, 62, w - 35, 62);
                g2.drawLine(35, h - 78, w - 35, h - 78);

                g2.setColor(new Color(95, 20, 90, 150));
                for (int x = 30; x < w - 30; x += 38) {
                    g2.fillRect(x, 100, 12, 3);
                    g2.fillRect(x, h - 116, 12, 3);
                }

                g2.dispose();
            }
        };

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 30, 45));
        contentPanel.setBounds(330, 95, 620, 420);

        JLabel title = createPixelLabel("LEVEL COMPLETE", 42, new Color(255, 145, 30));
        JLabel subtitle = createPixelLabel("THE GATE HAS OPENED", 18, new Color(235, 210, 190));
        JLabel message = createHtmlLabel(
                "Your soul survived the underworld.<br>Prepare for the next ascent.",
                18,
                new Color(255, 175, 70)
        );

        JLabel divider1 = createPixelLabel("◆ ━━━━━━━━━━━━━━━━━ ◆", 18, new Color(130, 55, 135));
        JLabel divider2 = createPixelLabel("◆ ━━━━━━━━━━━━━━━━━ ◆", 18, new Color(130, 55, 135));

        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(18));
        contentPanel.add(subtitle);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(divider1);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(message);
        contentPanel.add(Box.createVerticalStrut(35));
        contentPanel.add(divider2);

        add(contentPanel);

        LavaButton nextButton = new LavaButton("NEXT LEVEL");
        nextButton.setBounds(440, 555, 400, 64);
        nextButton.addActionListener(e -> {
            if (onNextLevel != null) onNextLevel.run();
        });
        add(nextButton);

        LavaButton restartButton = new LavaButton("RESTART LEVEL");
        restartButton.setBounds(440, 635, 190, 58);
        restartButton.addActionListener(e -> {
            if (onRestart != null) onRestart.run();
        });
        add(restartButton);

        LavaButton menuButton = new LavaButton("MENU");
        menuButton.setBounds(650, 635, 190, 58);
        menuButton.addActionListener(e -> {
            if (onBackToMenu != null) onBackToMenu.run();
        });
        add(menuButton);
    }

    private JLabel createPixelLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(color);
        label.setFont(new Font("Monospaced", Font.BOLD, size));
        return label;
    }

    private JLabel createHtmlLabel(String text, int size, Color color) {
        JLabel label = new JLabel("<html><div style='text-align:center;'>" + text + "</div></html>");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(color);
        label.setFont(new Font("Monospaced", Font.BOLD, size));
        return label;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundGif, 0, 0, getWidth(), getHeight(), this);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int glowAlpha = 50 + (int) (Math.sin(glowPulse) * 25);
        g2.setColor(new Color(255, 80, 0, Math.max(25, glowAlpha)));
        g2.fillOval(getWidth() / 2 - 230, 520, 460, 90);

        g2.setColor(new Color(255, 90, 0, 75));
        for (int i = 0; i < 55; i++) {
            int x = (i * 97) % Math.max(getWidth(), 1);
            int y = 120 + ((i * 43 + (int)(glowPulse * 25)) % Math.max(getHeight() - 140, 1));
            g2.fillRect(x, y, 3, 3);
        }

        g2.dispose();
    }

    private static class LavaButton extends JButton {
        private boolean hover = false;

        public LavaButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(new Color(255, 175, 70));
            setFont(new Font("Monospaced", Font.BOLD, 24));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            int w = getWidth();
            int h = getHeight();

            Polygon body = new Polygon();
            body.addPoint(25, 0);
            body.addPoint(w - 25, 0);
            body.addPoint(w, h / 2);
            body.addPoint(w - 25, h);
            body.addPoint(25, h);
            body.addPoint(0, h / 2);

            g2.setColor(hover ? new Color(45, 15, 20) : new Color(22, 8, 14));
            g2.fillPolygon(body);

            g2.setColor(new Color(85, 45, 55));
            g2.drawPolygon(body);

            g2.setColor(hover ? new Color(255, 160, 40) : new Color(255, 95, 0));
            g2.drawLine(35, 9, w - 35, 9);
            g2.drawLine(35, h - 10, w - 35, h - 10);

            g2.setColor(new Color(95, 55, 75));
            g2.fillRect(18, h / 2 - 10, 20, 20);
            g2.fillRect(w - 38, h / 2 - 10, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
