package ui;


import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
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
    private JPanel buttonPanel;

    // ─── Move History UI ────────────────────────────────────────────────────────
    private JPanel moveTrackPanel;

    // ─── Image Cache ────────────────────────────────────────────────────────────
    // All game sprites and UI images are loaded once and stored here by key
    private Map<String, Image> images = new HashMap<>();


    // ════════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // Sets up the panel, loads assets, builds the south UI (move track + buttons)
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
        setLayout(new BorderLayout());

        buildButtonPanel();
        buildSouthPanel();

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

    /** Builds the Restart / Next Level / Game Complete / Try Again button row. */
    private void buildButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        int btnFontSize = Math.max(14, Constants.TILE_SIZE / 5);

        restartButton = new JButton("Restart Level");
        restartButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        restartButton.addActionListener(e -> restartLevel());

        nextLevelButton = new JButton("Next Level");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        nextLevelButton.addActionListener(e -> nextLevel());

        gameCompleteButton = new JButton("Game Complete!");
        gameCompleteButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        gameCompleteButton.setBackground(new Color(50, 180, 80));
        gameCompleteButton.setForeground(Color.WHITE);
        gameCompleteButton.setOpaque(true);
        gameCompleteButton.setBorderPainted(false);
        gameCompleteButton.addActionListener(e -> {
            if (onGameComplete != null) onGameComplete.run();
        });

        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        tryAgainButton.addActionListener(e -> restartLevel());

        buttonPanel.add(restartButton);
        buttonPanel.add(nextLevelButton);
        buttonPanel.add(gameCompleteButton);
        buttonPanel.add(tryAgainButton);
    }

    /**
     * Builds the south area containing the move track panel (above)
     * and the button panel (below), and adds it to the BorderLayout SOUTH.
     */
    private void buildSouthPanel() {
        moveTrackPanel = createMoveTrack();

        JPanel moveTrackWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        moveTrackWrapper.setOpaque(false);
        moveTrackWrapper.add(moveTrackPanel);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new javax.swing.BoxLayout(southPanel, javax.swing.BoxLayout.Y_AXIS));
        southPanel.setOpaque(false);
        southPanel.add(moveTrackWrapper);
        southPanel.add(buttonPanel);

        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the move history track panel.
     * Displays one sprite per available move (based on shadowDelay).
     * Filled slots show the direction arrow image; empty slots show the default sprite.
     * Auto-sizes its width based on the current level's move count.
     */
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
                        // Draw the direction arrow for recorded moves
                        String arrowKey = getArrowKey(history.get(i).getType());
                        Image arrowImg = images.get(arrowKey);
                        if (arrowImg != null) {
                            g.drawImage(arrowImg, dx, dotY, dotSize, dotSize, this);
                        }
                    } else {
                        // Draw the empty slot sprite for unused moves
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
        loadGif("LADDER",        "ladder.gif");
        loadGif("DOOR_CLOSED",   "door_closed.gif");
        loadGif("DOOR_OPENED",   "door_opened.gif");
        loadGif("BUTTON_OFF",    "button_off.gif");
        loadGif("BUTTON_ON",     "button_on.gif");
        loadGif("PLAYER",        "player.gif");
        loadGif("SHADOW",        "shadow.gif");
    }

    /**
     * Resolves the assets directory by searching common relative paths
     * from the class file location. Falls back to "assets" in the working dir.
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

    /**
     * Loads and plays a WAV file on loop at 50% volume.
     * Uses the same asset resolution as images.
     */
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
        moveTrackPanel.revalidate();
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
            moveTrackPanel.revalidate();
            repaint();
        }
        requestFocusInWindow();
    }


    // ════════════════════════════════════════════════════════════════════════════
    // BUTTON VISIBILITY
    // Shows/hides the correct buttons depending on game state
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

        // Calculate tile size and grid offset to center the level on screen
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
                        break; // drawn after player/shadow
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
     * Buttons are drawn last so they visually appear above entities.
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
     * Draws the "Shadow appears in X moves" warning banner at the top of the screen.
     * Only shown while the shadow has not yet appeared.
     */
    private void drawShadowWarning(Graphics g, int tileSize) {
        Shadow shadow = game.getShadow();
        if (shadow.isActive()) return;

        int movesMade = game.getPlayer().getActionHistory().size();
        int movesRemaining = game.getCurrentLevel().getShadowDelay() - movesMade;
        if (movesRemaining <= 0) return;

        String warning = "Shadow appears in: " + movesRemaining + " move" + (movesRemaining == 1 ? "" : "s");
        int fontSize = Math.max(18, tileSize / 4);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        g.setColor(new Color(180, 0, 180));
        int textWidth = g.getFontMetrics().stringWidth(warning);
        int cx = getWidth() / 2;
        g.fillRoundRect(cx - textWidth / 2 - 10, 10, textWidth + 20, fontSize + 14, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString(warning, cx - textWidth / 2, fontSize + 10);
    }

    /** Draws the semi-transparent GAME OVER overlay when the player loses. */
    private void drawGameOverOverlay(Graphics g, int tileSize) {
        if (!game.isGameOver()) return;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        int fontSize = Math.max(48, tileSize / 2);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        String msg = "GAME OVER";
        int tw = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, getWidth() / 2 - tw / 2, getHeight() / 2 - fontSize / 2);
        g.setFont(new Font("Arial", Font.BOLD, fontSize / 2));
        g.setColor(Color.WHITE);
        String sub = "Click 'Try Again' to restart";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, getWidth() / 2 - sw / 2, getHeight() / 2 + fontSize / 2);
    }

    /** Draws the LEVEL COMPLETE or GAME COMPLETE overlay when the player wins. */
    private void drawLevelCompleteOverlay(Graphics g, int tileSize) {
        if (!game.isLevelComplete()) return;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        boolean isLastLevel = !engine.getLevelManager().hasNextLevel();
        int fontSize = Math.max(36, tileSize / 2);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        String msg = isLastLevel ? "GAME COMPLETE!" : "LEVEL COMPLETE!";
        g.setColor(isLastLevel ? new Color(100, 255, 150) : Color.GREEN);
        int tw = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, getWidth() / 2 - tw / 2, getHeight() / 2 - fontSize / 2);
        g.setFont(new Font("Arial", Font.BOLD, fontSize / 2));
        g.setColor(Color.WHITE);
        String sub = isLastLevel ? "Click 'Game Complete!' to finish" : "Click 'Next Level' to continue";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, getWidth() / 2 - sw / 2, getHeight() / 2 + fontSize / 2);
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