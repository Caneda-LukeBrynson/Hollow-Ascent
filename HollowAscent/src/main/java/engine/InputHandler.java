package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import model.ActionType;

public class InputHandler implements KeyListener {
    private ActionType lastAction;

    public InputHandler() {

        this.lastAction = null;
    }

    public ActionType getLastAction() {

        return lastAction;
    }

    public void clearAction() {

        lastAction = null;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        System.out.println("Key pressed: " + e.getKeyCode());

        switch (e.getKeyCode()) {

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:

                System.out.println("UP/W pressed");
                lastAction = ActionType.UP;
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:

                System.out.println("DOWN/S pressed");
                lastAction = ActionType.DOWN;
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:

                System.out.println("LEFT/A pressed");
                lastAction = ActionType.LEFT;
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:

                System.out.println("RIGHT/D pressed");
                lastAction = ActionType.RIGHT;
                break;
                
            default:
                System.out.println("Other key: " + e.getKeyCode());
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }
}