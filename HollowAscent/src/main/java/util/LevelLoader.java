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

        File projectRoot = new File(System.getProperty("user.dir"))

                .getParentFile() 
                .getParentFile() 
                .getParentFile() 
                .getParentFile(); 

        String basePath = projectRoot.getAbsolutePath() + File.separator;
        System.out.println("Looking for levels in: " + basePath);

        levels.add(loadLevel(basePath + "level1.txt"));
        levels.add(loadLevel(basePath + "level2.txt"));
        levels.add(loadLevel(basePath + "level3.txt"));

        return levels;
    }

    public static Level loadLevel(String filename) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;

            while ((line = reader.readLine()) != null) {

                lines.add(line);
            }

        } catch (IOException e) {

            System.err.println("File not found: " + filename);
            System.err.println("Working directory: " + System.getProperty("user.dir"));
            e.printStackTrace();

            return null;
        }

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
                char c = line.charAt(x);
                Tile tile;
                boolean walkable = true;

                switch (c) {

                    case '#':
                        walkable = false;
                        tile = new Tile(walkable, "WALL");
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
                        Door door = new Door(new Position(x, y));
                        doors.add(door);
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
            
            for (int x = 0; x < width; x++) {

                char c = line.charAt(x);

                if (c == 'L') {
                    Position top = new Position(x, y);
                    Position bottom = findLadderBottom(lines, x, y);

                    if (bottom != null) {

                        ladders.add(new Ladder(top, bottom));
                    }
                }
            }

        }

        for (int y = 0; y < height; y++) {

            String line = lines.get(y);

            for (int x = 0; x < width; x++) {

                char c = line.charAt(x);

                if (c == 'B') {

                    Button button = findLinkedButton(new Position(x, y), doors);
                    buttons.add(button);

                }
            }
        }

        return new Level(grid, buttons, ladders, doors, goal);
    }

    private static Position findLadderBottom(List<String> lines, int topX, int topY) {

        for (int y = topY + 1; y < lines.size(); y++) {

            if (y >= lines.size()) break;
            char c = lines.get(y).charAt(topX);

            if (c == 'L') {

                return new Position(topX, y);

            }
        }

        return new Position(topX, topY + 1);
    }

    private static Button findLinkedButton(Position buttonPos, List<Door> doors) {

        for (Door door : doors) {

            int dx = Math.abs(door.getPosition().getX() - buttonPos.getX());
            int dy = Math.abs(door.getPosition().getY() - buttonPos.getY());

            if (dx <= 2 && dy <= 2) {

                return new Button(buttonPos, door);
            }
        }

        return new Button(buttonPos, null);
    }

    public static Position getPlayerStart(String filename) {
        File projectRoot = new File(System.getProperty("user.dir"))
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile();

        String basePath = projectRoot.getAbsolutePath() + File.separator;

        try (BufferedReader reader = new BufferedReader(new FileReader(basePath + filename))) {
            String line;
            int y = 0;

            while ((line = reader.readLine()) != null) {

                for (int x = 0; x < line.length(); x++) {

                    if (line.charAt(x) == 'P') {

                        return new Position(x, y);
                    }
                }

                y++;
            }

        } catch (IOException e) {

            System.err.println("Player start file not found: " + basePath + filename);
            e.printStackTrace();
            
        }


        return new Position(1, 1);
    }
}
