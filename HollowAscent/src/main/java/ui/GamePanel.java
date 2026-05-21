package ui;


import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Constants;
import engine.Game;
import engine.GameEngine;
import model.Level;
import model.Tile;
import model.PlayerAction;
import model.ActionType;
import model.entity.Player;
import model.entity.Shadow;
import model.Position;
import model.object.Button;
import model.object.Door;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;


/**
 * GamePanel is the main rendering surface for the game.
 * It handles drawing the level grid, player, shadow, buttons,
 * move history UI, overlays (game over, level complete),
 * and the bottom button controls.
 */
public class GamePanel extends JPanel {

    // ─── Core References ────────────────────────────────────────────────────────
    private Game game;
    private GameEngine engine;
    private Runnable onGameComplete;

    // ─── UI Buttons ─────────────────────────────────────────────────────────────
    private JButton restartButton;
    private JButton nextLevelButton;
    private JButton gameCompleteButton;
    private JButton tryAgainButton;

    // ─── Move History UI ────────────────────────────────────────────────────────
    private JPanel moveTrackPanel;

    // ─── Image Cache ────────────────────────────────────────────────────────────
    private Map<String, Image> images = new HashMap<>();


    // ════════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════════
    public GamePanel(Game game, GameEngine engine, Runnable onGameComplete) {
        this.game = game;
        this.engine = engine;
        this.onGameComplete = onGameComplete;

        loadImages();
        playMusic("bg.wav");

        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new java.awt.Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null);

        restartButton = createInfernoButton("RESTART");
        restartButton.addActionListener(e -> restartLevel());

        nextLevelButton = createInfernoButton("NEXT LEVEL");
        nextLevelButton.addActionListener(e -> nextLevel());

        gameCompleteButton = createInfernoButton("CREDITS");
        gameCompleteButton.addActionListener(e -> {
            if (onGameComplete != null) onGameComplete.run();
        });

        tryAgainButton = createInfernoButton("TRY AGAIN");
        tryAgainButton.addActionListener(e -> restartLevel());

        add(restartButton);
        add(nextLevelButton);
        add(gameCompleteButton);
        add(tryAgainButton);

        moveTrackPanel = createMoveTrack();
        add(moveTrackPanel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        updateButtons();
    }


    // ════════════════════════════════════════════════════════════════════════════
    // UI CONSTRUCTION
    // ════════════════════════════════════════════════════════════════════════════

    /** Creates the move history track panel. */
    private JPanel createMoveTrack() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (game.getCurrentLevel() == null) return;

                int maxMoves = game.getCurrentLevel().getShadowDelay();
                List<PlayerAction> history = game.getPlayer().getActionHistory();
                int dotSize = 96;
                int dotGap = 6;
                int padding = 10;
                int startX = padding;
                int dotY = (getHeight() - dotSize) / 2;

                for (int i = 0; i < maxMoves; i++) {
                    int dx = startX + i * (dotSize + dotGap);
                    if (i < history.size()) {
                        String arrowKey = getArrowKey(history.get(i).getType());
                        Image arrowImg = images.get(arrowKey);
                        if (arrowImg != null) {
                            g.drawImage(arrowImg, dx, dotY, dotSize, dotSize, this);
                        }
                    } else {
                        Image slotImg = images.get("SLOT_EMPTY");
                        if (slotImg != null) {
                            g.drawImage(slotImg, dx, dotY, dotSize, dotSize, this);
                        }
                    }
                }
            }

            @Override
            public java.awt.Dimension getPreferredSize() {
                if (game.getCurrentLevel() == null) return new java.awt.Dimension(100, 96);
                int maxMoves = game.getCurrentLevel().getShadowDelay();
                int dotSize = 96;
                int dotGap = 6;
                int padding = 10;
                int width = padding * 2 + maxMoves * (dotSize + dotGap) - dotGap;
                return new java.awt.Dimension(width, 96);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /** Maps an ActionType to the corresponding arrow image key. */
    private String getArrowKey(ActionType type) {
        switch (type) {
            case UP:    return "ARROW_UP";
            case DOWN:  return "ARROW_DOWN";
            case LEFT:  return "ARROW_LEFT";
            case RIGHT: return "ARROW_RIGHT";
            default:    return "";
        }
    }

    /** Positions all action buttons and move track relative to current panel size. */
    private void positionActionButtons() {
        int panelWidth = Math.min(760, getWidth() - 120);
        int panelHeight = 390;
        int panelX = getWidth() / 2 - panelWidth / 2;
        int panelY = getHeight() / 2 - panelHeight / 2;
        int buttonHeight = 70;

        restartButton.setBounds(24, 24, 230, 56);
        nextLevelButton.setBounds(panelX + panelWidth / 2 + 20, panelY + panelHeight - 100, 300, buttonHeight);
        gameCompleteButton.setBounds(panelX + panelWidth / 2 + 20, panelY + panelHeight - 100, 300, buttonHeight);
        tryAgainButton.setBounds(getWidth() / 2 - 150, panelY + panelHeight - 100, 300, buttonHeight);

        if (game.isLevelComplete()) {
            restartButton.setBounds(panelX + panelWidth / 2 - 320, panelY + panelHeight - 100, 230, buttonHeight);
        }

        if (moveTrackPanel != null && game.getCurrentLevel() != null) {
            java.awt.Dimension ps = moveTrackPanel.getPreferredSize();
            int tx = (getWidth() - ps.width) / 2;
            int ty = getHeight() - ps.height - 12;
            moveTrackPanel.setBounds(tx, ty, ps.width, ps.height);
        }
    }

    /** Creates a styled inferno-themed button. */
    private JButton createInfernoButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

                Color top = getModel().isPressed() ? new Color(38, 6, 8) : new Color(24, 6, 12);
                Color bottom = getModel().isPressed() ? new Color(75, 18, 8) : new Color(48, 12, 10);
                Color edge = getModel().isRollover() ? new Color(255, 164, 36) : new Color(255, 93, 0);

                int[] xs = {18, getWidth() - 18, getWidth() - 2, getWidth() - 18, 18, 2};
                int[] ys = {5, 5, getHeight() / 2, getHeight() - 6, getHeight() - 6, getHeight() / 2};

                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillPolygon(new int[]{xs[0]+5,xs[1]+5,xs[2]+5,xs[3]+5,xs[4]+5,xs[5]+5},
                        new int[]{ys[0]+7,ys[1]+7,ys[2]+7,ys[3]+7,ys[4]+7,ys[5]+7}, 6);

                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillPolygon(xs, ys, 6);

                g2.setStroke(new BasicStroke(3));
                g2.setColor(edge);
                g2.drawPolygon(xs, ys, 6);

                g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(130, 45, 80));
                g2.drawLine(34, 17, getWidth() - 34, 17);
                g2.drawLine(34, getHeight() - 18, getWidth() - 34, getHeight() - 18);

                g2.setFont(titleFont(20));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(new Color(52, 7, 0));
                g2.drawString(getText(), tx + 2, ty + 2);
                g2.setColor(new Color(255, 184, 78));
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setForeground(new Color(255, 184, 78));
        return button;
    }


    // ════════════════════════════════════════════════════════════════════════════
    // FONT HELPERS
    // ════════════════════════════════════════════════════════════════════════════

    private Font titleFont(int size) {
        String[] preferred = {"Copperplate Gothic Bold", "Georgia", "Palatino Linotype", "Cambria", "Serif"};
        for (String name : preferred) {
            Font font = new Font(name, Font.BOLD, size);
            if (font.canDisplay('A')) return font;
        }
        return new Font(Font.SERIF, Font.BOLD, size);
    }

    private Font bodyFont(int size) {
        String[] preferred = {"Georgia", "Palatino Linotype", "Cambria", "Serif"};
        for (String name : preferred) {
            Font font = new Font(name, Font.BOLD, size);
            if (font.canDisplay('A')) return font;
        }
        return new Font(Font.SERIF, Font.BOLD, size);
    }


    // ════════════════════════════════════════════════════════════════════════════
    // ASSET LOADING
    // ════════════════════════════════════════════════════════════════════════════

    /** Loads an animated GIF using ImageIcon so all frames animate correctly. */
    private void loadGif(String key, String filename) {
        File assetsDir = resolveAssets();
        File file = new File(assetsDir, filename);
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        icon.setImageObserver(this);
        images.put(key, icon.getImage());
    }

    /** Loads a static image (PNG/JPG) using ImageIO. */
    private void loadImage(String key, String filename) {
        File assetsDir = resolveAssets();
        File file = new File(assetsDir, filename);
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                images.put(key, image);
            } else {
                System.err.println("Invalid image: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Could not load image: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /** Loads all game assets into the image cache. */
    private void loadImages() {
        loadGif("BACKGROUND",  "background.gif");
        loadImage("WALL",        "wall.png");
        loadImage("FLOOR_L",     "floor_left.png");
        loadImage("FLOOR_R",     "floor_right.png");
        loadImage("FLOOR_C",     "floor_center.png");
        loadImage("FLOOR_S",     "floor_single.png");
        loadImage("ARROW_UP",    "up.png");
        loadImage("ARROW_DOWN",  "down.png");
        loadImage("ARROW_LEFT",  "left.png");
        loadImage("ARROW_RIGHT", "right.png");
        loadImage("SLOT_EMPTY",  "def.png");
        loadGif("TUNNEL",        "Tunnel.gif");
        loadGif("GOAL",          "Goal.gif");
        loadGif("LADDER_P",      "ladder_plat.gif");
        loadGif("LADDER_L",      "ladder_l.gif");
        loadGif("LADDER_R",      "ladder_r.gif");
        loadGif("LADDER_S",      "ladder_s.gif");
        loadGif("LADDER",        "ladder.gif");
        loadGif("DOOR_CLOSED",   "door_closed.gif");
        loadGif("DOOR_OPENED",   "door_opened.gif");
        loadGif("BUTTON_OFF",    "button_off.gif");
        loadGif("BUTTON_ON",     "button_on.gif");
        loadGif("PLAYER",        "player.gif");
        loadGif("SHADOW",        "shadow.gif");
        loadGif("GRIM_REAPER",   "grim_reaper.gif");
        loadGif("GHOST",         "a_human_ghost_that_is_floating_wiggling_tail_same_expression_unknown.gif");
    }

    /**
     * Resolves the assets directory by searching common relative paths.
     * Falls back to "assets" in the working dir.
     */
    private File resolveAssets() {
        try {
            File classLocation = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            File root = classLocation.getParentFile().getParentFile().getParentFile();
            String[] candidates = {
                    "HollowAscent/assets",
                    "assets",
                    "../HollowAscent/assets",
                    "../assets"
            };
            for (String c : candidates) {
                File f = new File(root, c);
                if (f.exists()) {
                    System.out.println("Assets found: " + f.getAbsolutePath());
                    return f;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new File("assets");
    }


    // ════════════════════════════════════════════════════════════════════════════
    // AUDIO
    // ════════════════════════════════════════════════════════════════════════════

    /** Loads and plays a WAV file on loop at 50% volume. */
    private void playMusic(String filename) {
        try {
            File file = new File(resolveAssets(), filename);
            System.out.println("Loading music from: " + file.getAbsolutePath());
            System.out.println("File exists: " + file.exists());
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = volume.getMinimum();
            float max = volume.getMaximum();
            volume.setValue(min + (max - min) * 0.5f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.err.println("Could not load music: " + filename);
            e.printStackTrace();
        }
    }


    // ════════════════════════════════════════════════════════════════════════════
    // LEVEL CONTROL
    // ════════════════════════════════════════════════════════════════════════════

    /** Resets the current level: player position, shadow, buttons, and game state. */
    private void restartLevel() {
        Level currentLevel = game.getCurrentLevel();
        Position startPos = currentLevel.getPlayerSpawn();

        game.getPlayer().setPosition(new Position(startPos.getX(), startPos.getY()));
        game.getPlayer().setRenderX(startPos.getX());
        game.getPlayer().setRenderY(startPos.getY());
        game.getPlayer().clearHistory();

        game.getShadow().reset();

        for (model.object.Button btn : currentLevel.getButtons()) {
            btn.reset();
        }

        game.setGameOver(false);
        game.setLevelComplete(false);

        engine.restart();
        updateButtons();
        if (moveTrackPanel != null) moveTrackPanel.revalidate();
        repaint();
        requestFocusInWindow();
    }

    /** Advances to the next level and resets game state. */
    private void nextLevel() {
        if (engine.nextLevel()) {
            game.getPlayer().clearHistory();
            game.setLevelComplete(false);
            game.setGameOver(false);
            engine.restart();
            updateButtons();
            if (moveTrackPanel != null) moveTrackPanel.revalidate();
            repaint();
        }
        requestFocusInWindow();
    }


    // ════════════════════════════════════════════════════════════════════════════
    // BUTTON VISIBILITY
    // ════════════════════════════════════════════════════════════════════════════

    public void updateButtons() {
        if (game.isLevelComplete()) {
            boolean isLastLevel = !engine.getLevelManager().hasNextLevel();
            nextLevelButton.setVisible(!isLastLevel);
            gameCompleteButton.setVisible(isLastLevel);
            restartButton.setVisible(true);
            tryAgainButton.setVisible(false);
        } else if (game.isGameOver()) {
            nextLevelButton.setVisible(false);
            gameCompleteButton.setVisible(false);
            restartButton.setVisible(false);
            tryAgainButton.setVisible(true);
        } else {
            nextLevelButton.setVisible(false);
            gameCompleteButton.setVisible(false);
            restartButton.setVisible(true);
            tryAgainButton.setVisible(false);
        }
        positionActionButtons();
    }

    /** Called by the render timer each frame to refresh UI. */
    public void render() {
        updateButtons();
        if (moveTrackPanel != null) moveTrackPanel.repaint();
        repaint();
    }


    // ════════════════════════════════════════════════════════════════════════════
    // RENDERING
    // ════════════════════════════════════════════════════════════════════════════

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);

        Level level = game.getCurrentLevel();
        if (level == null || level.getGrid() == null) {
            g.setColor(Color.WHITE);
            g.drawString("Loading...", getWidth() / 2 - 60, getHeight() / 2);
            return;
        }

        Tile[][] grid = level.getGrid();
        int gridRows = grid.length;
        int gridCols = grid[0].length;
        int tileSize = Math.min(getWidth() / gridCols, getHeight() / gridRows);
        int offsetX = (getWidth() - tileSize * gridCols) / 2;
        int offsetY = (getHeight() - tileSize * gridRows) / 2;

        drawTiles(g, grid, gridRows, gridCols, tileSize, offsetX, offsetY, level);
        drawPlayer(g, tileSize, offsetX, offsetY);
        drawShadow(g, tileSize, offsetX, offsetY);
        drawButtons(g, grid, gridRows, gridCols, tileSize, offsetX, offsetY, level);
        drawShadowWarning(g, tileSize);
        drawGameOverOverlay(g, tileSize);
        drawLevelCompleteOverlay(g, tileSize);
    }

    /** Draws the background image, or black if not loaded. */
    private void drawBackground(Graphics g) {
        Image bg = images.get("BACKGROUND");
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /** Draws all tiles in the level grid. Buttons are skipped here (drawn separately). */
    private void drawTiles(Graphics g, Tile[][] grid, int gridRows, int gridCols,
                           int tileSize, int offsetX, int offsetY, Level level) {
        for (int y = 0; y < gridRows; y++) {
            for (int x = 0; x < gridCols; x++) {
                Tile tile = grid[y][x];
                if (tile == null) continue;

                String type = tile.getType();
                int px = offsetX + x * tileSize;
                int py = offsetY + y * tileSize;

                switch (type) {
                    case "WALL":
                        drawTile(g, "WALL", px, py, tileSize, new Color(0, 0, 0, 0));
                        break;
                    case "FLOOR_L":
                        drawTile(g, "FLOOR_L", px, py, tileSize, new Color(210, 180, 140));
                        break;
                    case "FLOOR_R":
                        drawTile(g, "FLOOR_R", px, py, tileSize, new Color(210, 180, 140));
                        break;
                    case "FLOOR_C":
                        drawTile(g, "FLOOR_C", px, py, tileSize, new Color(210, 180, 140));
                        break;
                    case "FLOOR_S":
                        drawTile(g, "FLOOR_S", px, py, tileSize, new Color(210, 180, 140));
                        break;
                    case "PLAYER_START":
                        drawTile(g, "TUNNEL", px, py, tileSize, new Color(50, 50, 50));
                        break;
                    case "AREA":
                        break;
                    case "GOAL":
                        drawTile(g, "GOAL", px, py, tileSize, Color.GREEN);
                        break;
                    case "LADDER_P":
                        drawTile(g, "LADDER_P", px, py, tileSize, new Color(139, 69, 19));
                        break;
                    case "LADDER_L":
                        drawTile(g, "LADDER_L", px, py, tileSize, new Color(139, 69, 19));
                        break;
                    case "LADDER_R":
                        drawTile(g, "LADDER_R", px, py, tileSize, new Color(139, 69, 19));
                        break;
                    case "LADDER_S":
                        drawTile(g, "LADDER_S", px, py, tileSize, new Color(139, 69, 19));
                        break;
                    case "LADDER":
                        drawTile(g, "LADDER", px, py, tileSize, new Color(139, 69, 19));
                        break;
                    case "DOOR": {
                        boolean open = false;
                        for (Door door : level.getDoors()) {
                            if (door.getPosition().getX() == x && door.getPosition().getY() == y) {
                                open = door.isOpen();
                                break;
                            }
                        }
                        drawTile(g, open ? "DOOR_OPENED" : "DOOR_CLOSED", px, py, tileSize,
                                open ? new Color(0, 210, 210) : new Color(70, 100, 180));
                        break;
                    }
                    case "BUTTON":
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Draws the player sprite at the interpolated render position.
     * Flips horizontally when facing left.
     */
    private void drawPlayer(Graphics g, int tileSize, int offsetX, int offsetY) {
        Player player = game.getPlayer();
        if (player == null || player.getPosition() == null) return;

        int spriteSize = tileSize * 7 / 10;
        int spriteOffset = (tileSize - spriteSize) / 2;
        int px = offsetX + (int)(player.getRenderX() * tileSize) + spriteOffset;
        int py = offsetY + (int)(player.getRenderY() * tileSize) + spriteOffset;
        Image img = images.get("PLAYER");

        if (img != null) {
            if (player.isFacingLeft()) {
                g.drawImage(img, px + spriteSize, py, -spriteSize, spriteSize, this);
            } else {
                g.drawImage(img, px, py, spriteSize, spriteSize, this);
            }
        } else {
            g.setColor(Color.BLUE);
            g.fillOval(px + 10, py + 10, spriteSize - 20, spriteSize - 20);
        }
    }

    /**
     * Draws the shadow sprite at the interpolated render position.
     * Only drawn when the shadow is active. Flips horizontally when facing left.
     */
    private void drawShadow(Graphics g, int tileSize, int offsetX, int offsetY) {
        Shadow shadow = game.getShadow();
        if (shadow == null || !shadow.isActive() || shadow.getPosition() == null) return;

        int shadowSize = tileSize * 7 / 10;
        int shadowOffset = (tileSize - shadowSize) / 2;
        int px = offsetX + (int)(shadow.getRenderX() * tileSize) + shadowOffset;
        int py = offsetY + (int)(shadow.getRenderY() * tileSize) + shadowOffset;
        Image img = images.get("SHADOW");

        if (img != null) {
            if (shadow.isFacingLeft()) {
                g.drawImage(img, px + shadowSize, py, -shadowSize, shadowSize, this);
            } else {
                g.drawImage(img, px, py, shadowSize, shadowSize, this);
            }
        } else {
            g.setColor(new Color(128, 0, 128, 180));
            g.fillOval(px + 10, py + 10, shadowSize - 20, shadowSize - 20);
        }
    }

    /**
     * Draws button tiles on top of the player and shadow.
     */
    private void drawButtons(Graphics g, Tile[][] grid, int gridRows, int gridCols,
                             int tileSize, int offsetX, int offsetY, Level level) {
        for (int by = 0; by < gridRows; by++) {
            for (int bx = 0; bx < gridCols; bx++) {
                Tile t = grid[by][bx];
                if (t == null || !t.getType().equals("BUTTON")) continue;

                boolean pressed = false;
                for (Button btn : level.getButtons()) {
                    if (btn.getPosition().getX() == bx && btn.getPosition().getY() == by) {
                        pressed = btn.isPressed();
                        break;
                    }
                }

                int px = offsetX + bx * tileSize;
                int py = offsetY + by * tileSize;
                int btnSize = tileSize * 7 / 10;
                int btnX = px + (tileSize - btnSize) / 2;
                int btnY = py + (tileSize - btnSize) / 2;
                drawTile(g, pressed ? "BUTTON_ON" : "BUTTON_OFF", btnX, btnY, btnSize,
                        pressed ? new Color(255, 220, 0) : new Color(180, 30, 30));
            }
        }
    }

    /**
     * Draws the shadow warning banner at the top of the screen.
     */
    private void drawShadowWarning(Graphics g, int tileSize) {
        Shadow shadow = game.getShadow();
        if (shadow.isActive()) return;

        int movesMade = game.getPlayer().getActionHistory().size();
        int movesRemaining = game.getCurrentLevel().getShadowDelay() - movesMade;
        if (movesRemaining <= 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        String line1 = "REAPER APPROACHES";
        String line2 = "IN  " + movesRemaining + (movesRemaining == 1 ? "  MOVE" : "  MOVES");

        Font f1 = titleFont(18);
        Font f2 = titleFont(26);

        g2.setFont(f2);
        int w2 = g2.getFontMetrics().stringWidth(line2);
        g2.setFont(f1);
        int w1 = g2.getFontMetrics().stringWidth(line1);

        int panelW = Math.max(w1, w2) + 48;
        int panelH = 88;
        int panelX = getWidth() - panelW - 24;
        int panelY = 16;

        // inferno panel background
        g2.setColor(new Color(0, 0, 0, 155));
        g2.fillRoundRect(panelX + 6, panelY + 7, panelW, panelH, 16, 16);
        g2.setPaint(new java.awt.GradientPaint(panelX, panelY, new Color(14, 0, 11, 220), panelX, panelY + panelH, new Color(42, 8, 8, 215)));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(255, 94, 0));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(128, 45, 86));
        g2.drawRoundRect(panelX + 6, panelY + 6, panelW - 12, panelH - 12, 10, 10);

        // text
        int cx = panelX + panelW / 2;
        g2.setFont(f1);
        FontMetrics fm1 = g2.getFontMetrics();
        g2.setColor(new Color(30, 0, 0, 180));
        g2.drawString(line1, cx - fm1.stringWidth(line1) / 2 + 2, panelY + 32 + 2);
        g2.setColor(new Color(235, 219, 206));
        g2.drawString(line1, cx - fm1.stringWidth(line1) / 2, panelY + 32);

        g2.setFont(f2);
        FontMetrics fm2 = g2.getFontMetrics();
        g2.setColor(new Color(52, 7, 0, 180));
        g2.drawString(line2, cx - fm2.stringWidth(line2) / 2 + 2, panelY + 66 + 2);
        g2.setColor(new Color(255, 145, 25));
        g2.drawString(line2, cx - fm2.stringWidth(line2) / 2, panelY + 66);

        g2.dispose();
    }

    /** Draws the inferno-styled GAME OVER overlay. */
    private void drawGameOverOverlay(Graphics g, int tileSize) {
        if (!game.isGameOver()) return;

        positionActionButtons();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int panelWidth = Math.min(760, getWidth() - 120);
        int panelHeight = 390;
        int panelX = getWidth() / 2 - panelWidth / 2;
        int panelY = getHeight() / 2 - panelHeight / 2;
        drawInfernoPanel(g2, panelX, panelY, panelWidth, panelHeight);

        Image reaper = images.get("GRIM_REAPER");
        if (reaper != null) {
            g2.drawImage(reaper, panelX + 52, panelY + 138, 170, 170, this);
        }

        drawCenteredText(g2, "THE ASCENT ENDS", panelY + 82, titleFont(46),
                new Color(255, 116, 22), new Color(45, 0, 0));
        drawCenteredText(g2, "The underworld has claimed your run.", panelY + 148, bodyFont(22),
                new Color(238, 222, 210), new Color(35, 0, 0));
        drawCenteredText(g2, "Gather yourself. Return to the climb.", panelY + 184, bodyFont(18),
                new Color(255, 174, 78), new Color(35, 0, 0));
        drawLavaDivider(g2, panelX + 250, panelY + 228, panelWidth - 500);

        g2.dispose();
    }

    /** Draws the inferno-styled LEVEL COMPLETE / GAME COMPLETE overlay. */
    private void drawLevelCompleteOverlay(Graphics g, int tileSize) {
        if (!game.isLevelComplete()) return;

        positionActionButtons();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        boolean isLastLevel = !engine.getLevelManager().hasNextLevel();

        int panelWidth = Math.min(760, getWidth() - 120);
        int panelHeight = 390;
        int panelX = getWidth() / 2 - panelWidth / 2;
        int panelY = getHeight() / 2 - panelHeight / 2;

        drawInfernoPanel(g2, panelX, panelY, panelWidth, panelHeight);

        Image ghost = images.get("GHOST");
        if (ghost != null) {
            g2.drawImage(ghost, panelX + panelWidth - 158, panelY + 132, 112, 112, this);
        }

        String msg = isLastLevel ? "GAME COMPLETE" : "LEVEL COMPLETE";
        drawCenteredText(g2, msg, panelY + 82, titleFont(46),
                new Color(255, 145, 25), new Color(65, 8, 0));
        drawLavaDivider(g2, panelX + 165, panelY + 116, panelWidth - 330);

        String sub = isLastLevel ? "The final gate opens." : "The gate opens. Continue your ascent.";
        drawCenteredText(g2, sub, panelY + 168, bodyFont(22),
                new Color(238, 222, 210), new Color(35, 0, 0));

        String prompt = isLastLevel ? "Meet the team Behind the Underworld" : "Step forward into the next trial.";
        drawCenteredText(g2, prompt, panelY + 205, bodyFont(18),
                new Color(255, 174, 78), new Color(35, 0, 0));
        drawLavaDivider(g2, panelX + 250, panelY + 248, panelWidth - 500);

        g2.dispose();
    }

    /** Draws the inferno-styled dark panel background. */
    private void drawInfernoPanel(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(0, 0, 0, 155));
        g2.fillRoundRect(x + 12, y + 14, w, h, 22, 22);
        g2.setPaint(new GradientPaint(x, y, new Color(14, 0, 11, 238), x, y + h, new Color(42, 8, 8, 235)));
        g2.fillRoundRect(x, y, w, h, 22, 22);
        g2.setStroke(new BasicStroke(4));
        g2.setColor(new Color(255, 94, 0));
        g2.drawRoundRect(x, y, w, h, 22, 22);
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(128, 45, 86));
        g2.drawRoundRect(x + 12, y + 12, w - 24, h - 24, 16, 16);
    }

    /** Draws a lava-styled divider line with a glowing center diamond. */
    private void drawLavaDivider(Graphics2D g2, int x, int y, int w) {
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(116, 40, 82, 180));
        g2.drawLine(x, y, x + w, y);
        g2.setColor(new Color(255, 94, 0, 190));
        g2.fillRect(x + w / 2 - 4, y - 4, 8, 8);
        g2.setColor(new Color(255, 180, 55, 160));
        g2.drawLine(x + 18, y + 5, x + w - 18, y + 5);
    }

    /** Draws centered text with a drop shadow. */
    private void drawCenteredText(Graphics2D g2, String text, int baselineY, Font font, Color fill, Color shadow) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int x = getWidth() / 2 - fm.stringWidth(text) / 2;
        g2.setColor(shadow);
        g2.drawString(text, x + 3, baselineY + 3);
        g2.setColor(fill);
        g2.drawString(text, x, baselineY);
    }

    /**
     * Helper to draw a tile image at a given position and size.
     * Falls back to a solid color rectangle if the image isn't loaded.
     */
    private void drawTile(Graphics g, String imageKey, int px, int py, int size, Color fallback) {
        Image img = images.get(imageKey);
        if (img != null) {
            g.drawImage(img, px, py, size, size, this);
        } else {
            g.setColor(fallback);
            g.fillRect(px, py, size, size);
        }
    }
}