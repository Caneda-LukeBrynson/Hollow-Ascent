package model.object;

import model.Position;

public class Button {

    private Position position;
    private Door linkedDoor;
    private boolean pressed;
    private boolean inverted;

    public Button(Position position, Door linkedDoor) {
        this.position = position;
        this.linkedDoor = linkedDoor;
        this.pressed = false;
        this.inverted = false;
    }

    public Button(Position position, Door linkedDoor, boolean inverted) {
        this.position = position;
        this.linkedDoor = linkedDoor;
        this.pressed = false;
        this.inverted = inverted;
    }

    public void update(boolean someoneStanding) {
        pressed = someoneStanding;

        if (linkedDoor != null) {
            if (inverted) {
                if (someoneStanding) {
                    linkedDoor.open();
                } else {
                    linkedDoor.close();
                }
            } else {
                if (someoneStanding) {
                    linkedDoor.close();
                } else {
                    linkedDoor.open();
                }
            }
        }
    }

    public void reset() {
        pressed = false;
        if (linkedDoor != null) {
            if (inverted) {
                linkedDoor.close();
            } else {
                linkedDoor.open();
            }
        }
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

    public boolean isInverted() {
        return inverted;
    }
}