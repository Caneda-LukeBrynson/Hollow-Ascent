public class PlayerAction {
    private ActionType type;
    private long timestamp;

    public PlayerAction(ActionType type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public ActionType getType() { return type; }
    public long getTimestamp() { return timestamp; }
}