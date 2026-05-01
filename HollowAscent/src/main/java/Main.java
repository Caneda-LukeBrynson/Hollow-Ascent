public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            GameEngine engine = new GameEngine(frame);
            engine.startGame();
        });
    }
}
