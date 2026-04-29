// Constants.java
public class Constants {
    public static final int TILE_SIZE = 64;
    public static final int GRID_ROWS = 10;
    public static final int GRID_COLS = 10;
    public static final int SHADOW_DELAY = 3;
    public static final int MAX_LEVELS = 5;
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 640;
}

// Position.java (in util)
public class Position {
    private int x;
    private int y;
}

// CollisionManager.java
public class CollisionManager {
    public boolean isWalkable(Level level, Position pos) {}
}

// InputHandler.java
public class InputHandler {
    public ActionType getAction(KeyEvent e) {}
}

// GameFrame.java (in ui)
public class GameFrame extends JFrame {
    public GameFrame() {}
}

// GamePanel.java (in ui)
public class GamePanel extends JPanel {
    public void render(Game game) {}
    protected void paintComponent(Graphics g) {}
}