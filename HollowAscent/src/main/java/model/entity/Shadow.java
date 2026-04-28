public class Shadow extends Entity {
    private List<PlayerAction> actionsToReplay;
    private int delayTicks;
    private int currentIndex;

    public Shadow(Position position, int delayTicks) {
        super(position);
        this.actionsToReplay = new ArrayList<>();
        this.delayTicks = delayTicks;
        this.currentIndex = 0;
    }

    @Override
    public void move(ActionType action) {
        // logic to be added in Week 2
    }

    public void update() {
        // replay logic to be added in Week 2
    }

    public void setActionsToReplay(List<PlayerAction> actions) {
        this.actionsToReplay = actions;
    }

    public int getDelayTicks() { return delayTicks; }
    public int getCurrentIndex() { return currentIndex; }
}