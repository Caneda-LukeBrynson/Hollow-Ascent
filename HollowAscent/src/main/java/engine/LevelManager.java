public class LevelManager {

    private List<Level> levels;
    private int currentIndex;

    public LevelManager(List<Level> levels) {
        this.levels = levels;
        this.currentIndex = 0;
    }

    public Level loadLevel(int index) {
        if (index >= 0 && index < levels.size()) {
            currentIndex = index;
            return levels.get(index);
        }
        return null;
    }

    public Level nextLevel() {
        if (hasNextLevel()) {
            currentIndex++;
            return levels.get(currentIndex);
        }
        return null;
    }

    public boolean hasNextLevel() {
        return currentIndex + 1 < levels.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public List<Level> getLevels() {
        return levels;
    }
}
