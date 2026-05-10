package ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import util.Constants;

public class GameFrame extends JFrame {

    public GameFrame() {

        setTitle(Constants.GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {

            gd.setFullScreenWindow(this); 

        } else {
            
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setVisible(true); 
        setResizable(false);
    }
}
