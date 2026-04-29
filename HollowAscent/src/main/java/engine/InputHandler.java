public class InputHandler implements KeyListener {
    private ActionType lastAction;

    public InputHandler() {
        this.lastAction = null;
    }

    public ActionType getLastAction() { return lastAction; }
    public void clearAction() { lastAction = null; }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    lastAction = ActionType.UP; break;
            case KeyEvent.VK_DOWN:  lastAction = ActionType.DOWN; break;
            case KeyEvent.VK_LEFT:  lastAction = ActionType.LEFT; break;
            case KeyEvent.VK_RIGHT: lastAction = ActionType.RIGHT; break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}