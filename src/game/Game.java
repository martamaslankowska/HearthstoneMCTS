package game;

import players.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Player activePlayer;
    private Player inactivePlayer;
    private int move;


    public Game(Player activePlayer, Player inactivePlayer) {
        this.activePlayer = activePlayer;
        this.inactivePlayer = inactivePlayer;
        this.move = 0;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }


    private void printActivePlayerState() {
        System.out.println("\nMOVE " + move + " - round: " + (move + 1)/2);
        System.out.println(activePlayer + " turn:");
        System.out.println("  HAND: " + activePlayer.getHand());
        System.out.println("  WARRIORS: " + activePlayer.getWarriors());
    }


    private void preparePlayersBeforeGame() {
        activePlayer.prepareDeck();
        activePlayer.setHand(new ArrayList<>(activePlayer.getDeck().subList(0, 3)));
        activePlayer.setDeck(new ArrayList<>(activePlayer.getDeck().subList(3, activePlayer.getDeck().size())));

        inactivePlayer.prepareDeck();
        inactivePlayer.setHand(new ArrayList<>(inactivePlayer.getDeck().subList(0, 4)));
        inactivePlayer.setDeck(new ArrayList<>(inactivePlayer.getDeck().subList(4, inactivePlayer.getDeck().size())));
    }


    public Player gamePlay(boolean verbose) {
        if (verbose)
            System.out.println(activePlayer + " starts the game with 3 cards\n" +
                    inactivePlayer + " has 4 cards in return and comes second");

        preparePlayersBeforeGame();
        while (!gameFinished()) {
            move++;
            activePlayer.setMana(Math.min((move + 1)/2, 10));
            activePlayer.hit();
            if (gameFinished())
                break;

            if (verbose)
                printActivePlayerState();

            List<Card> selectedCardsToPlay = activePlayer.selectCardsToPlay(activePlayer.getPossibleCardsToPlay());
            activePlayer.playCards(selectedCardsToPlay, true);
            changeActivePlayer();
        }

        return getWinner();
    }

    private boolean gameFinished() {
        return activePlayer.getHp() <= 0 || inactivePlayer.getHp() <= 0;
    }

    private Player getWinner() {
        assert gameFinished();
        if (activePlayer.getHp() <= 0)
            return inactivePlayer;
        else
            return activePlayer;
    }

    private void changeActivePlayer() {
        Player deactivatedPlayer = activePlayer;
        activePlayer = inactivePlayer;
        inactivePlayer = deactivatedPlayer;
    }

}
