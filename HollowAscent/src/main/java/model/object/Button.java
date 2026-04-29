public class Button {
    private Position position;
    private Door linkedDoor;
    private boolean pressed;

    public Button(Position position, Door linkedDoor) {

        this.position = position;
        this.linkedDoor = linkedDoor;
        this.pressed = false;
    }

    public void onStep() {

        pressed = true;
        linkedDoor.close(); // change was done here
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