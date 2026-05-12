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
import java.util.Map;
import util.Constants;
import engine.Game;
import engine.GameEngine;
import model.Level;
import model.Tile;
import model.entity.Player;
import model.entity.Shadow;
import model.Position;
import model.object.Button;
import model.object.Door;

public class GamePanel extends JPanel {
    private Game game;
    private GameEngine engine;
    private JButton restartButton;
    private JButton nextLevelButton;
    private JButton tryAgainButton;
    private JPanel buttonPanel;

    private Map<String, Image> images = new HashMap<>();

    public GamePanel(Game game, GameEngine engine) {

        this.game = game;
        this.engine = engine;
        loadImages();

        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new java.awt.Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        int btnFontSize = Math.max(14, Constants.TILE_SIZE / 5);

        restartButton = new JButton("Restart Level");
        restartButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        restartButton.addActionListener(e -> restartLevel());

        nextLevelButton = new JButton("Next Level");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        nextLevelButton.addActionListener(e -> nextLevel());

        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        tryAgainButton.addActionListener(e -> restartLevel());

        buttonPanel.add(restartButton);
        buttonPanel.add(nextLevelButton);
        buttonPanel.add(tryAgainButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }

        });

        updateButtons();
    }

    private void loadImages() {

        loadImage("BACKGROUND",  "background.jpg");
        loadImage("WALL",        "wall.png");
        loadImage("FLOOR",       "floor.png");
        loadImage("TUNNEL",      "tunnel.jpg");
        loadImage("GOAL",        "Goal.png");
        loadImage("LADDER",      "ladder.png");
        loadImage("DOOR_CLOSED", "gate_opened.png");
        loadImage("DOOR_OPEN",   "gate_closed.png");
        loadImage("BUTTON_OFF",  "button.png");
        loadImage("BUTTON_ON",   "button.png");
        loadImage("PLAYER",      "player.png");
        loadImage("SHADOW",      "shadow.png");
    }

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

    private void loadImage(String key, String filename) {

        File assetsDir = resolveAssets();
        File file = new File(assetsDir, filename);

        if (file.exists()) {

            images.put(key, new ImageIcon(file.getAbsolutePath()).getImage());

        } else {

            System.err.println("Could not load image: " + file.getAbsolutePath());
        }
    }

    private void drawTile(Graphics g, String imageKey, int px, int py, int size, Color fallback) {

        Image img = images.get(imageKey);

        if (img != null) {

            g.drawImage(img, px, py, size, size, this);

        } else {

            g.setColor(fallback);
            g.fillRect(px, py, size, size);

        }
    }

    private void restartLevel() {
        Level currentLevel = game.getCurrentLevel();
        Position startPos = currentLevel.getPlayerSpawn();

        game.getPlayer().setPosition(new Position(startPos.getX(), startPos.getY()));
        game.getPlayer().clearHistory();

        game.getShadow().reset();

        for (model.object.Button btn : currentLevel.getButtons()) {

            btn.reset();
        }

        game.setGameOver(false);
        game.setLevelComplete(false);

        engine.restart();
        updateButtons();
        repaint();
        requestFocusInWindow();
    }

    private void nextLevel() {

        if (engine.nextLevel()) {

            game.getPlayer().clearHistory();
            game.getShadow().reset();
            game.setLevelComplete(false);
            game.setGameOver(false);
            engine.restart();
            updateButtons();
            repaint();

        }

        requestFocusInWindow();
    }

    public void updateButtons() {

        if (game.isLevelComplete()) {

            nextLevelButton.setVisible(true);
            restartButton.setVisible(true);
            tryAgainButton.setVisible(false);

        } else if (game.isGameOver()) {

            nextLevelButton.setVisible(false);
            restartButton.setVisible(false);
            tryAgainButton.setVisible(true);

        } else {

            nextLevelButton.setVisible(false);
            restartButton.setVisible(true);
            tryAgainButton.setVisible(false);

        }
    }

    public void render() {

        updateButtons();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Image bg = images.get("BACKGROUND");
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        Level level = game.getCurrentLevel();

        if (level == null || level.getGrid() == null) {

            g.setColor(Color.WHITE);
            g.drawString("Loading...", getWidth() / 2 - 60, getHeight() / 2);
            return;

        }

        Tile[][] grid = level.getGrid();
        int gridRows = grid.length;
        int gridCols = grid[0].length;

        int tileW = getWidth() / gridCols;
        int tileH = getHeight() / gridRows;
        int tileSize = Math.min(tileW, tileH);

        int totalW = tileSize * gridCols;
        int totalH = tileSize * gridRows;
        int offsetX = (getWidth() - totalW) / 2;
        int offsetY = (getHeight() - totalH) / 2;

        for (int y = 0; y < gridRows; y++) {

            for (int x = 0; x < gridCols; x++) {

                Tile tile = grid[y][x];
                if (tile == null) continue;

                String type = tile.getType();
                int px = offsetX + x * tileSize;
                int py = offsetY + y * tileSize;

                switch (type) {
                    case "WALL":
                        drawTile(g, "WALL", px, py, tileSize, Color.DARK_GRAY);
                        break;

                    case "FLOOR":
                        drawTile(g, "FLOOR", px, py, tileSize, new Color(210, 180, 140));
                        break;

                    case "PLAYER_START":
                        drawTile(g, "TUNNEL", px, py, tileSize, new Color(50, 50, 50));
                        break;

                    case "AREA":
                        break;

                    case "GOAL":
                        drawTile(g, "GOAL", px, py, tileSize, Color.GREEN);
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

                        drawTile(g, open ? "DOOR_CLOSED" : "DOOR_OPEN", px, py, tileSize,
                                open ? new Color(0, 210, 210) : new Color(70, 100, 180));
                        break;

                    }

                    case "BUTTON": {

                        boolean pressed = false;

                        for (Button btn : level.getButtons()) {

                            if (btn.getPosition().getX() == x && btn.getPosition().getY() == y) {
                                pressed = btn.isPressed();
                                break;

                            }
                        }

                        drawTile(g, pressed ? "BUTTON_ON" : "BUTTON_OFF", px, py, tileSize,
                                pressed ? new Color(255, 220, 0) : new Color(180, 30, 30));
                        break;

                    }

                    default:
                        break;

                }
            }
        }

        Player player = game.getPlayer();

        if (player != null && player.getPosition() != null) {
            Position pos = player.getPosition();
            int px = offsetX + pos.getX() * tileSize;
            int py = offsetY + pos.getY() * tileSize;
            Image img = images.get("PLAYER");

            if (img != null) {

                g.drawImage(img, px, py, tileSize, tileSize, this);

            } else {

                g.setColor(Color.BLUE);
                g.fillOval(px + 10, py + 10, tileSize - 20, tileSize - 20);

            }
        }

        Shadow shadow = game.getShadow();

        if (shadow != null && shadow.isActive() && shadow.getPosition() != null) {

            Position pos = shadow.getPosition();
            int px = offsetX + pos.getX() * tileSize;
            int py = offsetY + pos.getY() * tileSize;
            Image img = images.get("SHADOW");

            if (img != null) {

                g.drawImage(img, px, py, tileSize, tileSize, this);

            } else {

                g.setColor(new Color(128, 0, 128, 180));
                g.fillOval(px + 10, py + 10, tileSize - 20, tileSize - 20);

            }
        }

        if (!shadow.isActive()) {

            int movesMade = game.getPlayer().getActionHistory().size();
            int movesRemaining = Constants.SHADOW_DELAY - movesMade;

            if (movesRemaining > 0) {

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
        }

        if (game.isGameOver()) {

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

        if (game.isLevelComplete()) {

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.GREEN);

            int fontSize = Math.max(36, tileSize / 2);

            g.setFont(new Font("Arial", Font.BOLD, fontSize));

            String msg = "LEVEL COMPLETE!";
            int tw = g.getFontMetrics().stringWidth(msg);

            g.drawString(msg, getWidth() / 2 - tw / 2, getHeight() / 2 - fontSize / 2);
            g.setFont(new Font("Arial", Font.BOLD, fontSize / 2));
            g.setColor(Color.WHITE);

            String sub = "Click 'Next Level' to continue";
            int sw = g.getFontMetrics().stringWidth(sub);

            g.drawString(sub, getWidth() / 2 - sw / 2, getHeight() / 2 + fontSize / 2);
        }
    }
}
