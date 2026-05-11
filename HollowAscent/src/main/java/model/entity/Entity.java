package model.entity;

import model.Position;

public abstract class Entity {
    protected Position position;

    public Entity(Position position) {

        this.position = position;
    }

    public Position getPosition() {

        return position;
    }

    public void setPosition(Position position) {
        
        this.position = position;
    }
}