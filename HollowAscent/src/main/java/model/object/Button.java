package model.object;

import model.Position;

public class Button {

    private Position position;
    private Door linkedDoor;
    private boolean pressed;

    public Button(Position position, Door linkedDoor) {

        this.position = position;
        this.linkedDoor = linkedDoor;
        this.pressed = false;

    }

    public void update(boolean somoneStanding) {

        pressed = somoneStanding;

        if (linkedDoor != null) {

            if (somoneStanding) {

                linkedDoor.close();

            } else {
                
                linkedDoor.open();
            }
        }

    }

    public void reset() {

        pressed = false;
        if (linkedDoor != null) linkedDoor.open();

    }

    public Position getPosition() {

        return position;
    }

    public boolean isPressed() {

        return pressed;
    }

    public Door getLinkedDoor() {

        return linkedDoor;
    }
}