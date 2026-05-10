package util;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class Constants {
    public static final int GRID_ROWS = 10;
    public static final int GRID_COLS = 10;
    public static final int SHADOW_DELAY = 2;
    public static final int MAX_LEVELS = 5;
    public static final int FPS = 60;
    public static final int TICK_DELAY = 1000 / FPS;
    public static final String GAME_TITLE = "Hollow Ascent";

    public static final int SCREEN_WIDTH;
    public static final int SCREEN_HEIGHT;
    public static final int TILE_SIZE;
    public static final int GAME_AREA_SIZE;
    public static final int GAME_OFFSET_X;

    public static final int WINDOW_WIDTH;
    public static final int WINDOW_HEIGHT;

    static {

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
                
        SCREEN_WIDTH  = gd.getDisplayMode().getWidth();
        SCREEN_HEIGHT = gd.getDisplayMode().getHeight();

        TILE_SIZE     = SCREEN_HEIGHT / GRID_ROWS;
        GAME_AREA_SIZE = TILE_SIZE * GRID_ROWS;
        GAME_OFFSET_X  = (SCREEN_WIDTH - GAME_AREA_SIZE) / 2;

        WINDOW_WIDTH  = SCREEN_WIDTH;
        WINDOW_HEIGHT = SCREEN_HEIGHT;
    }
}
