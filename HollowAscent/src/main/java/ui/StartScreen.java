package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class StartScreen extends JPanel {

    private Runnable onStartGame;

    public StartScreen(Runnable onStartGame) {

        this.onStartGame = onStartGame;
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

        JLabel subtitle = new JLabel("A shadow puzzle game");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitle.setForeground(new Color(150, 130, 200));
        add(subtitle, gbc);

        gbc.insets = new Insets(30, 0, 10, 0);
        add(new JLabel(" "), gbc);

        gbc.insets = new Insets(10, 0, 10, 0);
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 22));
        startButton.setPreferredSize(new Dimension(200, 55));
        startButton.setBackground(new Color(80, 50, 130));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setFocusable(true);

        startButton.addMouseListener(new MouseAdapter() {
            @Override

            public void mouseEntered(MouseEvent e) {

                startButton.setBackground(new Color(110, 70, 180));
                startButton.requestFocusInWindow();
            }

            @Override
            public void mouseExited(MouseEvent e) {

                startButton.setBackground(new Color(80, 50, 130));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                onStartGame.run();
                
            }
        });

        startButton.addActionListener(e -> onStartGame.run());
        add(startButton, gbc);
    }
}
