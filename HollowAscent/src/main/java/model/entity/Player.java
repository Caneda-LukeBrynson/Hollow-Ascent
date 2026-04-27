public class Player extends Entity {
    private List<PlayerAction> actionHistory;

    public Player(Position position) {
        super(position);
        this.actionHistory = new ArrayList<>();
    }

    @Override
    public void move(ActionType action) {
        // logic to be added in Week 2
    }

    public void recordAction(ActionType action) {
        actionHistory.add(new PlayerAction(action, System.currentTimeMillis()));
    }

    public List<PlayerAction> getActionHistory() { return actionHistory; }
    public void clearHistory() { actionHistory.clear(); }
}