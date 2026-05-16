package model.entity;

import model.ActionType;
import model.PlayerAction;
import model.Position;
import java.util.ArrayList;
import java.util.List;

public class Shadow {
    private Position position;
    private Position startPosition;
    private List<PlayerAction> actionsToReplay;
    private int delayTicks;
    private int currentIndex;
    private boolean active;
    private int lastPlayerActionCount;

    public Shadow(Position position, int delayTicks) {
        
        this.startPosition = new Position(position.getX(), position.getY());
        this.position = new Position(position.getX(), position.getY());
        this.actionsToReplay = new ArrayList<>();
        this.delayTicks = delayTicks;
        this.currentIndex = 0;
        this.active = false;
        this.lastPlayerActionCount = 0;
    }

    public Position getPosition() {

        return position;
    }

    public void setPosition(Position position) {

        this.position = position;
    }

    public void move(ActionType action) {

        switch (action) {

            case UP:    position = new Position(position.getX(), position.getY() - 1); break;

            case DOWN:  position = new Position(position.getX(), position.getY() + 1); break;

            case LEFT:  position = new Position(position.getX() - 1, position.getY()); break;

            case RIGHT: position = new Position(position.getX() + 1, position.getY()); break;

            default: break;
        }
    }

    public void update(int currentPlayerActionCount) {

        if (!active) return;

        if (currentPlayerActionCount <= lastPlayerActionCount) return;

        if (currentIndex < actionsToReplay.size()) {

            PlayerAction action = actionsToReplay.get(currentIndex);
            move(action.getType());
            currentIndex++;
            lastPlayerActionCount = currentPlayerActionCount;
        }
    }

    public void setActionsToReplay(List<PlayerAction> actions) {

        this.actionsToReplay = new ArrayList<>(actions);

        if (actions.size() >= delayTicks && !active) {

            active = true;
            position = new Position(startPosition.getX(), startPosition.getY());
            currentIndex = 0;
            lastPlayerActionCount = actions.size() - delayTicks;

        }
    }

    public void reset() {

        position = new Position(startPosition.getX(), startPosition.getY());

        active = false;
        currentIndex = 0;

        actionsToReplay.clear();
        lastPlayerActionCount = 0;

    }

    public void setDelayTicks(int delayTicks) {

        this.delayTicks = delayTicks;
    }

    public boolean isActive() {

        return active;
    }

    public int getDelayTicks() {

        return delayTicks;
    }

    public int getCurrentIndex() {

        return currentIndex;
    }

    public Position getStartPosition() {

        return startPosition;
    }
}