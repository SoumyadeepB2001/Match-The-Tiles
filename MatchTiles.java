import javax.swing.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;

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

    // Array to hold image icons for the tiles
    ImageIcon[] tileImages = new ImageIcon[10];

    public static void main(String[] args) {
        new MatchTiles();
    }

    MatchTiles() {
        frame = new JFrame("Match The Tiles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
        initComponents();
        frame.setVisible(true); // Make the frame visible
    }

    private void initComponents() {
        // Set up the menu bar and menu items
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

        buttonPanel = new JPanel(new GridLayout(5, 4)); // 5 rows and 4 columns for 20 buttons
        frame.add(buttonPanel, BorderLayout.CENTER);

        loadTileImages(); // Load images
        startGame();
    }

    private void loadTileImages() {
        // Load 10 different images into the tileImages array (assuming you have images in a folder named "images")
        for (int i = 0; i < 10; i++) {
            tileImages[i] = new ImageIcon("images/tile" + (i + 1) + ".png"); // Example: "images/tile1.png"
        }
    }

    public void startGame() {
        // Randomizing the tiles numbering (or image indices in this case)
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i); // Add each image index twice (since we need pairs)
            numbers.add(i);
        }
        Collections.shuffle(numbers); // Shuffle the image indices

        // Adding all the tiles (buttons)
        for (int i = 0; i < 20; i++) {
            tiles[i] = new JButton(); // Create an empty button
            tiles[i].setIcon(tileImages[numbers.get(i)]); // Set the shuffled image
            tiles[i].setFont(new Font("Serif", Font.BOLD, 44));
            tiles[i].setBackground(Color.WHITE);
            buttonPanel.add(tiles[i]); // Add buttons to the panel
        }

        Timer timer = new Timer(3000, e -> {
            // Clear the icons after 3 seconds (hide the images)
            for (int i = 0; i < 20; i++) {
                tiles[i].setIcon(null);
            }

            // Add action listeners to the buttons
            for (int i = 0; i < 20; i++) {
                addTilesActionListeners(numbers, i);
            }
        });
        timer.setRepeats(false); // Only execute once
        timer.start(); // Start the timer

        frame.revalidate();
        frame.repaint();
    }

    public void addTilesActionListeners(ArrayList<Integer> numbers, int buttonIndex) {
        tiles[buttonIndex].addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isATileSelected) {
                            tiles[buttonIndex].setIcon(tileImages[numbers.get(buttonIndex)]); // Show image for second tile
                            removeActionListener(tiles[buttonIndex]);
                            int indexOfFirstTile = numbers.get(indexOfSelectedTile);
                            int indexOfSecondTile = numbers.get(buttonIndex);

                            if (indexOfFirstTile == indexOfSecondTile) {
                                // Matched case
                                isATileSelected = false;
                                indexOfSelectedTile = -1;
                                noOfMatchedTiles += 2; // Increase matched tile count
                            } else {
                                // Mismatched case
                                for (JButton tile : tiles) {
                                    removeActionListener(tile);
                                }

                                Timer timer = new Timer(800, new ActionListener() {
                                    public void actionPerformed(ActionEvent evt) {
                                        tiles[buttonIndex].setIcon(null); // Hide image on second tile
                                        tiles[indexOfSelectedTile].setIcon(null); // Hide image on first tile
                                        isATileSelected = false;
                                        indexOfSelectedTile = -1;

                                        // Re-enable buttons
                                        for (int i = 0; i < 20; i++) {
                                            addTilesActionListeners(numbers, i);
                                        }
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }

                            noOfMoves++;
                            labNoOfMoves.setText("Moves: " + noOfMoves); // Update moves
                        } else {
                            isATileSelected = true;
                            indexOfSelectedTile = buttonIndex;
                            tiles[buttonIndex].setIcon(tileImages[numbers.get(buttonIndex)]); // Show image for first tile
                            removeActionListener(tiles[buttonIndex]);
                        }
                    }
                });
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
