package engine;

import model.Level;
import model.Tile;
import model.object.Ladder;
import model.Position;

public class CollisionManager {

    public boolean isWalkable(Level level, Position pos) {

        if (isOutOfBounds(level, pos)) {
            return false;
        }

        Tile tile = level.getTile(pos.getX(), pos.getY());

        return tile != null && tile.isWalkable();
    }


    public boolean isOutOfBounds(Level level, Position pos) {

        if (level == null || level.getGrid() == null) {

            return true;
        }

        int rows = level.getGrid().length;

        if (rows == 0) {

            return true;
        }

        int cols = level.getGrid()[0].length;

        return pos.getX() < 0 || pos.getX() >= cols || pos.getY() < 0 || pos.getY() >= rows;

    }

    public boolean isOnLadder(Level level, Position pos) {

        if (isOutOfBounds(level, pos)) {
            return false;
        }


        for (Ladder ladder : level.getLadders()) {


            if (ladder.isAtLadder(pos)) {
                return true;

            }
        }
        
        return false;
    }
}