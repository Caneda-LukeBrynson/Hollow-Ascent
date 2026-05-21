package model.entity;

import model.ActionType;
import model.PlayerAction;
import model.Position;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private Position position;
    private List<PlayerAction> actionHistory;
    private float renderX;
    private float renderY;

    public Player(Position position) {
        this.position = position;
        this.actionHistory = new ArrayList<>();
        this.renderX = position.getX();
        this.renderY = position.getY();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void move(ActionType action) {
        switch (action) {
            case UP:
                position = new Position(position.getX(), position.getY() - 1);
                break;
            case DOWN:
                position = new Position(position.getX(), position.getY() + 1);
                break;
            case LEFT:
                position = new Position(position.getX() - 1, position.getY());
                break;
            case RIGHT:
                position = new Position(position.getX() + 1, position.getY());
                break;
            default:
                break;
        }
    }

    public void recordAction(ActionType action) {
        actionHistory.add(new PlayerAction(action, (int) System.currentTimeMillis()));
    }

    public List<PlayerAction> getActionHistory() {
        return actionHistory;
    }

    public void clearHistory() {
        actionHistory.clear();
    }

    public float getRenderX() { return renderX; }
    public float getRenderY() { return renderY; }
    public void setRenderX(float x) { renderX = x; }
    public void setRenderY(float y) { renderY = y; }
    public void updateRenderPosition(float speed) {
        renderX += (position.getX() - renderX) * speed;
        renderY += (position.getY() - renderY) * speed;
    }

    private boolean facingLeft = false;

    public boolean isFacingLeft() { return facingLeft; }
    public void setFacingLeft(boolean facingLeft) { this.facingLeft = facingLeft; }
}