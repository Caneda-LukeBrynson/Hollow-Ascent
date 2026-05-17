package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;


public class GameCompleteScreen extends JPanel {


    private Runnable onReturnToMenu;


    public GameCompleteScreen(Runnable onReturnToMenu) {


        this.onReturnToMenu = onReturnToMenu;
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 30));
        setFocusable(true);
        buildUI();


    }


    @Override
    public void addNotify() {


        super.addNotify();
        requestFocusInWindow();


    }


    private void buildUI() {


        GridBagConstraints gbc = new GridBagConstraints();


        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);


        JLabel title = new JLabel("Hollow Ascent");
        title.setFont(new Font("Arial", Font.BOLD, 56));
        title.setForeground(new Color(220, 200, 255));
        add(title, gbc);


        JLabel congrats = new JLabel("Congratulations on finishing the game!");
        congrats.setFont(new Font("Arial", Font.BOLD, 28));
        congrats.setForeground(new Color(100, 255, 150));
        add(congrats, gbc);


        JLabel subtitle = new JLabel("You escaped the hollow. The ascent is complete.");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitle.setForeground(new Color(150, 130, 200));
        add(subtitle, gbc);


        gbc.insets = new Insets(30, 0, 10, 0);
        add(new JLabel(" "), gbc);


        gbc.insets = new Insets(10, 0, 10, 0);
        JButton menuButton = new JButton("Return to Menu");
        menuButton.setFont(new Font("Arial", Font.BOLD, 22));
        menuButton.setPreferredSize(new Dimension(220, 55));
        menuButton.setBackground(new Color(80, 50, 130));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setBorderPainted(false);
        menuButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuButton.setFocusable(true);


        menuButton.addMouseListener(new MouseAdapter() {


            @Override
            public void mouseEntered(MouseEvent e) {


                menuButton.setBackground(new Color(110, 70, 180));
                menuButton.requestFocusInWindow();
            }


            @Override
            public void mouseExited(MouseEvent e) {


                menuButton.setBackground(new Color(80, 50, 130));
            }


            @Override
            public void mouseClicked(MouseEvent e) {
               
                onReturnToMenu.run();
            }
        });


        menuButton.addActionListener(e -> onReturnToMenu.run());
        add(menuButton, gbc);
    }
}
