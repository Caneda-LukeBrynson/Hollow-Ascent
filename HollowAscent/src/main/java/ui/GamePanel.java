public class GamePanel extends JPanel {
    private Game game;

    public GamePanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // rendering logic to be added in Week 2
    }

    public void render() {
        repaint();
    }
}