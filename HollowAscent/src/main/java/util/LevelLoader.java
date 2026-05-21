package util;

import model.Level;
import model.Tile;
import model.object.Button;
import model.object.Door;
import model.object.Ladder;
import model.object.Goal;
import model.Position;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    public static List<Level> loadLevels() {
        List<Level> levels = new ArrayList<>();
        String basePath = System.getProperty("user.dir") + File.separator;

        levels.add(loadLevel(basePath + "level1.txt", 2));
        levels.add(loadLevel(basePath + "level2.txt", 5));
        levels.add(loadLevel(basePath + "level3.txt", 9));

        return levels;
    }

    public static Level loadLevel(String filename, int shadowDelay) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("File not found: " + filename);
            e.printStackTrace();
            return null;
        }

        if (lines.isEmpty()) return null;

        return parseLevel(lines, shadowDelay);
    }

    private static Level parseLevel(List<String> lines, int shadowDelay) {
        int height = lines.size();
        int width = lines.get(0).length();

        Tile[][] grid = new Tile[height][width];
        List<Button> buttons = new ArrayList<>();
        List<Ladder> ladders = new ArrayList<>();
        List<Door> doors = new ArrayList<>();
        Goal goal = null;

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = (x < line.length()) ? line.charAt(x) : ' ';
                Tile tile;

                switch (c) {
                    case '#':
                        tile = new Tile(false, "WALL");
                        break;
                    case 'P':
                        tile = new Tile(true, "PLAYER_START");
                        break;
                    case 'G':
                        tile = new Tile(true, "GOAL");
                        goal = new Goal(new Position(x, y));
                        break;
                    case 'A':
                        tile = new Tile(true, "AREA");
                        break;
                    case '[':
                        tile = new Tile(true, "FLOOR_L");
                        break;
                    case ']':
                        tile = new Tile(true, "FLOOR_R");
                        break;
                    case '-':
                        tile = new Tile(true, "FLOOR_C");
                        break;
                    case '=':
                        tile = new Tile(true, "FLOOR_S");
                        break;
                    case 'O':
                        tile = new Tile(true, "LADDER_P");
                        break;
                    case 'L':
                        tile = new Tile(true, "LADDER");
                        break;
                    case 'D':
                        tile = new Tile(true, "DOOR");
                        doors.add(new Door(new Position(x, y)));
                        break;
                    case 'B':
                        tile = new Tile(true, "BUTTON");
                        break;
                    default:
                        tile = new Tile(true, "FLOOR");
                        break;
                }

                grid[y][x] = tile;
            }
        }

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == 'L') {
                    Position top = new Position(x, y);
                    Position bottom = findLadderBottom(lines, x, y);
                    ladders.add(new Ladder(top, bottom));
                }
            }
        }

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == 'B') {
                    buttons.add(findLinkedButton(new Position(x, y), doors));
                }
            }
        }

        return new Level(grid, buttons, ladders, doors, goal, shadowDelay);
    }

    private static Position findLadderBottom(List<String> lines, int topX, int topY) {
        for (int y = topY + 1; y < lines.size(); y++) {
            String row = lines.get(y);
            if (topX < row.length() && row.charAt(topX) == 'L') {
                return new Position(topX, y);
            }
        }
        return new Position(topX, topY + 1);
    }

    private static Button findLinkedButton(Position buttonPos, List<Door> doors) {
        for (Door door : doors) {
            int dx = Math.abs(door.getPosition().getX() - buttonPos.getX());
            int dy = Math.abs(door.getPosition().getY() - buttonPos.getY());
            if (dx <= 2 && dy <= 2) return new Button(buttonPos, door);
        }
        return new Button(buttonPos, null);
    }

    public static Position getPlayerStart(String filename) {
        Level level = loadLevel(filename, 2);
        return (level != null) ? level.getPlayerSpawn() : new Position(1, 1);
    }
}