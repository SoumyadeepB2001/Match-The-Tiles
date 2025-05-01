import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class MatchTiles implements ActionListener {
    JFrame frame;
    JMenuBar menuBar;
    JMenu options, help;
    JMenuItem newGame, exit, contact, rules, about;
    JLabel labNoOfMoves;
    JPanel buttonPanel;
    JButton tiles[] = new JButton[20];

    boolean isATileSelected = false;
    int indexOfSelectedTile = -1;
    int noOfMoves = 0;
    int noOfMatchedTiles = 0;

    ImageIcon[] tileImages = new ImageIcon[10];
    ImageIcon woodBack = new ImageIcon("images/woodBack.png");

    public static void main(String[] args) {
        new MatchTiles();
    }

    MatchTiles() {
        frame = new JFrame("Match The Tiles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        initComponents();
        frame.setVisible(true);
    }

    private void initComponents() {
        menuBar = new JMenuBar();
        options = new JMenu("Options");
        help = new JMenu("Help");
        newGame = new JMenuItem("New Game");
        exit = new JMenuItem("Exit");
        contact = new JMenuItem("Contact");
        rules = new JMenuItem("Rules");
        about = new JMenuItem("About");

        newGame.addActionListener(this);
        exit.addActionListener(this);
        contact.addActionListener(this);
        rules.addActionListener(this);
        about.addActionListener(this);

        options.add(newGame);
        options.add(exit);
        help.add(contact);
        help.add(rules);
        help.add(about);

        menuBar.add(options);
        menuBar.add(help);

        frame.setJMenuBar(menuBar);

        labNoOfMoves = new JLabel("Moves: 0");
        labNoOfMoves.setFont(new Font("Arial", Font.BOLD, 24));
        labNoOfMoves.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(labNoOfMoves, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridLayout(5, 4));
        frame.add(buttonPanel, BorderLayout.CENTER);

        loadTileImages();
        startGame();
    }

    private void loadTileImages() {
        for (int i = 0; i < 10; i++) {
            tileImages[i] = new ImageIcon("images/tile" + (i + 1) + ".png");
        }
    }

    public void startGame() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i);
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        for (int i = 0; i < 20; i++) {
            tiles[i] = new JButton();
            tiles[i].setIcon(tileImages[numbers.get(i)]);
            tiles[i].setFont(new Font("Serif", Font.BOLD, 44));
            tiles[i].setBackground(Color.WHITE);
            tiles[i].setBorder(new LineBorder(Color.BLACK, 3));
            buttonPanel.add(tiles[i]);
        }

        Timer timer = new Timer(3000, e -> {
            for (int i = 0; i < 20; i++) {
                tiles[i].setIcon(woodBack);
            }
            for (int i = 0; i < 20; i++) {
                addTilesActionListeners(numbers, i);
            }
        });
        timer.setRepeats(false);
        timer.start();

        frame.revalidate();
        frame.repaint();
    }

    public void addTilesActionListeners(ArrayList<Integer> numbers, int buttonIndex) {
        tiles[buttonIndex].addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        playSound("audio/wood.wav");

                        if (!tiles[buttonIndex].isEnabled()) return;

                        if (isATileSelected) {
                            tiles[buttonIndex].setIcon(tileImages[numbers.get(buttonIndex)]);
                            removeActionListener(tiles[buttonIndex]);
                            int indexOfFirstTile = numbers.get(indexOfSelectedTile);
                            int indexOfSecondTile = numbers.get(buttonIndex);

                            if (indexOfFirstTile == indexOfSecondTile) {
                                tiles[buttonIndex].setDisabledIcon(tileImages[numbers.get(buttonIndex)]);
                                tiles[indexOfSelectedTile].setDisabledIcon(tileImages[numbers.get(indexOfSelectedTile)]);
                                tiles[buttonIndex].setEnabled(false);
                                tiles[indexOfSelectedTile].setEnabled(false);

                                isATileSelected = false;
                                indexOfSelectedTile = -1;
                                noOfMatchedTiles += 2;
                            } else {
                                for (JButton tile : tiles) {
                                    removeActionListener(tile);
                                }

                                Timer timer = new Timer(800, new ActionListener() {
                                    public void actionPerformed(ActionEvent evt) {
                                        tiles[buttonIndex].setIcon(woodBack);
                                        tiles[indexOfSelectedTile].setIcon(woodBack);
                                        isATileSelected = false;
                                        indexOfSelectedTile = -1;
                                        for (int i = 0; i < 20; i++) {
                                            if (tiles[i].isEnabled()) {
                                                addTilesActionListeners(numbers, i);
                                            }
                                        }
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }

                            noOfMoves++;
                            labNoOfMoves.setText("Moves: " + noOfMoves);
                        } else {
                            isATileSelected = true;
                            indexOfSelectedTile = buttonIndex;
                            tiles[buttonIndex].setIcon(tileImages[numbers.get(buttonIndex)]);
                            removeActionListener(tiles[buttonIndex]);
                        }
                    }
                });
    }

    public void playSound(String soundFileName) {
        try {
            File soundFile = new File(soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void removeActionListener(JButton button) {
        for (ActionListener listener : button.getActionListeners())
            button.removeActionListener(listener);
    }

    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "New Game":
                frame.dispose();
                new MatchTiles();
                break;
            case "Exit":
                System.exit(0);
                break;
            case "Contact":
                try {
                    Desktop.getDesktop().browse(new URL("https://twitter.com/SoumyadeepB2001").toURI());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Browser not found");
                }
                break;
            case "Rules":
                JOptionPane.showMessageDialog(null,
                        "Flip two tiles at a time to find matching pairs, removing the pairs from the board, and continue until all tiles are matched and cleared.");
                break;
            case "About":
                JOptionPane.showMessageDialog(null,
                        "Match The Tiles Game\nVersion: 1.0.1\nProgram written by Soumyadeep Banerjee, MCA");
                break;
        }
    }
}