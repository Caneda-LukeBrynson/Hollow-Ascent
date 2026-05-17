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
import ui.GameCompleteScreen;
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
                GamePanel panel = new GamePanel(game, engine, () -> {
             
                    frame.getContentPane().removeAll();
                    GameCompleteScreen completeScreen = new GameCompleteScreen(() -> {
                       
                        frame.getContentPane().removeAll();
                        List<Level> freshLevels = LevelLoader.loadLevels();
                        LevelManager freshManager = new LevelManager(freshLevels);
                        StartScreen newStart = new StartScreen(() -> {
                            frame.getContentPane().removeAll();
                            Level fl = freshManager.loadLevel(0);
                            Position ps = fl.getPlayerSpawn();
                            Player p = new Player(ps);
                            Shadow sh = new Shadow(new Position(ps.getX(), ps.getY()), fl.getShadowDelay());
                            Game g2 = new Game(p, sh);
                            g2.setCurrentLevel(fl);
                            GameEngine eng2 = new GameEngine(g2, freshManager);
                            GamePanel panel2 = new GamePanel(g2, eng2, null);
                            InputHandler ih2 = eng2.getInputHandler();
                            frame.addKeyListener(ih2);
                            panel2.addKeyListener(ih2);
                            frame.add(panel2);
                            frame.revalidate();
                            frame.repaint();
                            panel2.requestFocusInWindow();
                            eng2.start();
                            Timer rt2 = new Timer(16, ev -> panel2.render());
                            rt2.start();


                        });


                        frame.add(newStart);
                        frame.revalidate();
                        frame.repaint();


                    });


                    frame.add(completeScreen);
                    frame.revalidate();
                    frame.repaint();


                });


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
