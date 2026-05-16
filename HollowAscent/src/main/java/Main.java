import engine.Game;
import engine.GameEngine;
import engine.LevelManager;
import engine.InputHandler;
import model.Level;
import model.Position;
import model.entity.Player;
import model.entity.Shadow;
import ui.GameFrame;
import ui.GamePanel;
import ui.StartScreen;
import util.Constants;
import util.LevelLoader;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("Working dir: " + System.getProperty("user.dir"));
        System.out.println("level1 exists: " + new java.io.File("level1.txt").exists());
        System.out.println("level1 abs path: " + new java.io.File("level1.txt").getAbsolutePath());
        
        SwingUtilities.invokeLater(() -> {


            List<Level> levels = LevelLoader.loadLevels();
            LevelManager levelManager = new LevelManager(levels);

            GameFrame frame = new GameFrame();

            frame.getRootPane().registerKeyboardAction(

                    e -> System.exit(0),
                    KeyStroke.getKeyStroke("ESCAPE"),
                    JComponent.WHEN_IN_FOCUSED_WINDOW

            );


            StartScreen startScreen = new StartScreen(() -> {

                frame.getContentPane().removeAll();

                Level firstLevel = levelManager.loadLevel(0);
                Position playerStart = firstLevel.getPlayerSpawn();
                Position shadowStart = new Position(playerStart.getX(), playerStart.getY());

                Player player = new Player(playerStart);
                Shadow shadow = new Shadow(shadowStart, firstLevel.getShadowDelay());

                Game game = new Game(player, shadow);
                game.setCurrentLevel(firstLevel);

                GameEngine engine = new GameEngine(game, levelManager);
                GamePanel panel = new GamePanel(game, engine);

                InputHandler inputHandler = engine.getInputHandler();
                frame.addKeyListener(inputHandler);
                panel.addKeyListener(inputHandler);

                frame.add(panel);
                frame.revalidate();
                frame.repaint();
                panel.requestFocusInWindow();

                engine.start();

                Timer renderTimer = new Timer(16, e -> panel.render());
                renderTimer.start();
                
            });

            frame.add(startScreen);
            frame.revalidate();
            frame.repaint();
        });
    }
}
