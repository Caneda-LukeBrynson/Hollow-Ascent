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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    public static List<Level> loadLevels() {
        List<Level> levels = new ArrayList<>();
        levels.add(loadLevel("level1.txt"));
        levels.add(loadLevel("level2.txt"));
        levels.add(loadLevel("level3.txt"));
        return levels;
    }

    public static Level loadLevel(String filename) {
        List<String> lines = null;

        InputStream is = LevelLoader.class.getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            is = LevelLoader.class.getClassLoader().getResourceAsStream("/" + filename);
        }
        if (is != null) {
            lines = readLines(is, filename);
        }

        if (lines == null) {
            File f = new File(System.getProperty("user.dir"), filename);
            if (f.exists()) lines = readLines(f, filename);
        }

        if (lines == null) {
            File f = new File(System.getProperty("user.dir") + File.separator + "HollowAscent", filename);
            if (f.exists()) lines = readLines(f, filename);
        }

        if (lines == null) {
            try {
                File classRoot = new File(LevelLoader.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());
                File candidate = new File(classRoot.isDirectory() ? classRoot : classRoot.getParentFile(), filename);
                if (candidate.exists()) lines = readLines(candidate, filename);
            } catch (Exception ignored) {}
        }

        if (lines == null || lines.isEmpty()) {
            System.err.println("[LevelLoader] Could not find level file: " + filename);
            return null;
        }

        return parseLevel(lines);
    }


    private static List<String> readLines(InputStream is, String label) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("[LevelLoader] Error reading stream for: " + label);
            e.printStackTrace();
            return null;
        }
        return lines.isEmpty() ? null : lines;
    }

    private static List<String> readLines(File file, String label) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("[LevelLoader] Error reading file: " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        return lines.isEmpty() ? null : lines;
    }


    private static Level parseLevel(List<String> lines) {
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
                    case 'F':
                        tile = new Tile(true, "FLOOR");
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

        return new Level(grid, buttons, ladders, doors, goal);
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
        Level level = loadLevel(filename);
        return (level != null) ? level.getPlayerSpawn() : new Position(1, 1);
    }
}
