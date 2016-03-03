import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

/**
 * 
 *
 * @author Armand Ghaffarpour
 * 
 * This is the memory class handling the GUI and game rules.
 *
 */
public class Memory extends JFrame implements ActionListener {
    private JPanel outerPanel, cardPanel, scorePanel, menuPanel;
    private JPanel[] playerPanel;
    private JButton startButton, exitButton, newGameButton;
    private JSpinner playerSpinner, rowSpinner, columnSpinner;
    private JLabel[] playerLabel, scoreLabel;
    private File bildmapp;
    private File[] bilder;
    private Card[] k, cardsInGame;
    private int nbrOfPlayers, rows, columns, nbrOfCards, turn, cardOne, cardTwo;
    private int activePlayer, cardsLeft, winner;
    private int[] score;
    private boolean timer;

    /**
     * This is the constructor. Initialize as many cards as there are pictures
     * so that they can be used in the game. Creates the start menu first.
     */
    public Memory() {
        setTitle("Memory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        bildmapp = new File("bildmapp");
        bilder = bildmapp.listFiles();
        k = new Card[bilder.length];
        for (int i=0; i<bilder.length; i++) {
            k[i] = new Card(new ImageIcon(bilder[i].getPath()), Card.Status.HIDDEN);
        }
        
        outerPanel = new JPanel(new BorderLayout());
        makeStartMenu();
        add(outerPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true); 
    }

    /**
     * Creates the start menu for the user to put in number of players, number
     * of rows and number of columns. The number of pictures available to us
     * limits the number of rows to five and the number of columns to six.
     */
    private void makeStartMenu() {
        JPanel insertPanel = new JPanel(new GridLayout(6,1));
        JPanel menuPanel = new JPanel(new FlowLayout());
        
        JLabel insertPlayersLabel = new JLabel("Insert number of players");
        JLabel insertRowsLabel = new JLabel("Insert the number of rows");
        JLabel insertColumnsLabel = new JLabel("Insert the number of columns");
        //Creates spinners for the input
        playerSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 6, 1));
        rowSpinner = new JSpinner(new SpinnerNumberModel(4, 4, 5, 1));
        columnSpinner = new JSpinner(new SpinnerNumberModel(4, 4, 6, 1));
        
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        
        insertPanel.add(insertPlayersLabel);
        insertPanel.add(playerSpinner);
        insertPanel.add(insertRowsLabel);
        insertPanel.add(rowSpinner);
        insertPanel.add(insertColumnsLabel);
        insertPanel.add(columnSpinner);
        
        menuPanel.add(startButton);
        menuPanel.add(exitButton);
        
        outerPanel.add(insertPanel, BorderLayout.CENTER);
        outerPanel.add(menuPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the play board after the user has input everything from the start
     * menu. The number of cards are calculated depending on the number of rows
     * and columns so that its an even number.
     * After everything is set up it will call a new game. 
     */
    private void makePlayBoard() {
        //The spinner makes sure we always get valid numbers as an input
        nbrOfPlayers = (Integer) playerSpinner.getValue();
        rows = (Integer) rowSpinner.getValue();
        columns = (Integer) columnSpinner.getValue();
        
        //Make sure the number of cards that should fit the board is an even number.
        nbrOfCards = rows * columns;
        if (nbrOfCards%2 != 0) {
            nbrOfCards--;
        }
        cardsLeft = nbrOfCards;
        cardsInGame = new Card[nbrOfCards];
        score = new int[nbrOfPlayers];
        
        scorePanel = new JPanel(new GridLayout(nbrOfPlayers, 1));
        playerPanel = new JPanel[nbrOfPlayers];
        cardPanel = new JPanel(new GridLayout(rows, columns));
        menuPanel = new JPanel(new FlowLayout());
        
        //Create the players view, showing each players score and whose turn it is.
        playerLabel = new JLabel[nbrOfPlayers];
        scoreLabel = new JLabel[nbrOfPlayers];
        for (int i=0; i<nbrOfPlayers; i++) {
            playerLabel[i] = new JLabel("Player " + (i+1));
            scoreLabel[i] = new JLabel("Score: " + score[i]);
            playerPanel[i] = new JPanel();
            playerPanel[i].setLayout(new GridLayout(2,1));
            playerPanel[i].setBackground(Color.LIGHT_GRAY);
            playerPanel[i].add(playerLabel[i]);
            playerPanel[i].add(scoreLabel[i]);
            scorePanel.add(playerPanel[i]);
        }
        //Set the active player's background to yellow
        playerPanel[activePlayer].setBackground(Color.yellow);
        
        //Create menu
        newGameButton = new JButton("Restart");
        newGameButton.addActionListener(this);
        menuPanel.add(newGameButton);
        menuPanel.add(exitButton);
        
        //Put the panels in the outer panel
        outerPanel.add(scorePanel, BorderLayout.WEST);
        outerPanel.add(cardPanel, BorderLayout.CENTER);
        outerPanel.add(menuPanel, BorderLayout.SOUTH);
        
        newGame();
    }
    
    /**
     * Creates a new game of memory, resetting every score and creates a new
     * set of memory cards to be used in the game.
     */
    private void newGame() {
        //Reset the cardboard
        cardPanel.removeAll();
        //Reset the score
        for (int i=0; i<nbrOfPlayers; i++) {
            score[i] = 0;
            scoreLabel[i].setText("Score: " + score[i]);
        }
        //Reset the number of cards left
        cardsLeft = nbrOfCards;
        
        //Changes the active player to be the first player
        playerPanel[activePlayer].setBackground(Color.LIGHT_GRAY);
        activePlayer = 0;
        playerPanel[0].setBackground(Color.yellow);
        
        Tools.randomOrdering(k);
        for(int i = 0; i < nbrOfCards/2; i++){
            cardsInGame[i] = k[i];
            cardsInGame[i + (nbrOfCards/2)] = k[i].copy();
        }
        Tools.randomOrdering(cardsInGame);
        
        for (int i=0; i<nbrOfCards; i++) {
            cardsInGame[i].setStatus(Card.Status.HIDDEN);
            cardPanel.add(cardsInGame[i]);
            cardsInGame[i].addActionListener(this);
            cardsInGame[i].setActionCommand("" + i);
        }
    }
    
    /**
     * Handles when the user presses the first and the second card. When the
     * user has pressed the second card a timer will count down from 1.5 seconds
     * and after that the players turn will end. If the card pressed is not
     * faced down or the timer is still counting then nothing will happen.
     * @param card The card that the player pressed.
     */
    private void turn(int card) {
        if (cardsInGame[card].getStatus() == Card.Status.HIDDEN && !timer) {
            cardsInGame[card].setStatus(Card.Status.VISIBLE);
            switch (turn) {
            case 0:
                cardOne = card;
                turn++;
                break;
            case 1:
                cardTwo = card;
                Timer clock = new Timer(1500, this);
                clock.setActionCommand("-5");
                clock.setRepeats(false);
                clock.start();
                timer = true;
                break;
            }
        }
    }
    
    /**
     * Handles what happens after the timer has finished counting. If the player
     * has found two similar cards then their score will increase, the cards
     * left will decrease and the player can choose two new cards. If the cards
     * are different then it will be the next players turn.
     * If there are no cards left then the winner will be calculated and a
     * dialog window will show who won. After that a new game will start.
     * This method does not handle a tie.
     */
    private void endTurn() {
        turn = 0;
        if (cardsInGame[cardOne].sammaBild(cardsInGame[cardTwo])) {
            cardsInGame[cardOne].setStatus(Card.Status.MISSING);
            cardsInGame[cardTwo].setStatus(Card.Status.MISSING);
            score[activePlayer]++;
            scoreLabel[activePlayer].setText("Score: " + score[activePlayer]);
            cardsLeft = cardsLeft - 2;
        } else {
            cardsInGame[cardOne].setStatus(Card.Status.HIDDEN);
            cardsInGame[cardTwo].setStatus(Card.Status.HIDDEN);
            changePlayer();
        }
        
        //Handle when there is no cards left
        if (cardsLeft == 0) {
            winner = 0;
            for (int i=0; i<nbrOfPlayers; i++) {
                if (score[winner] < score[i]) {
                    winner = i;
                }
            }
            JOptionPane.showMessageDialog(null, "Player " + (winner+1) + " won! "
                    + "\nPress OK to start a new game.");
            newGame();
            setVisible(true);
        }
    }
    
    /**
     * Changes the current active player to the next player.
     */
    private void changePlayer(){
        playerPanel[activePlayer].setBackground(Color.LIGHT_GRAY);
        activePlayer = (activePlayer + 1) % nbrOfPlayers;
        playerPanel[activePlayer].setBackground(Color.YELLOW);
    }

    /**
     * Handles the action for the menu buttons, the cards and the timer.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            outerPanel.removeAll();
            makePlayBoard();
            setVisible(true);
        }
        else if (e.getSource() == exitButton) {
            System.exit(0);
        }
        else if (e.getSource() == newGameButton) {
            newGame();
            setVisible(true);
        }
        else {
            int pressedCard = Integer.parseInt(e.getActionCommand());
            if (pressedCard >= 0) {
                turn(pressedCard);
            }
            else if (pressedCard == -5) {
                timer = false;
                endTurn();
            }
        }
    }

    public static void main(String[] args) {
        new Memory();
    }
}