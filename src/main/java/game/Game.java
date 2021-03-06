package game;

import attacks.Attack;
import mctstemplate.MCTPlayer;
import players.MCTSPlayer;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Player activePlayer;
    private Player inactivePlayer;
    private int move;
    public List<Long> decisionTimes=new ArrayList<>();


    public Game(Player activePlayer, Player inactivePlayer) {
        this.activePlayer = activePlayer;
        this.inactivePlayer = inactivePlayer;
        this.move = 0;
    }


    private void printActivePlayerState() {
        System.out.println("\nMOVE " + move + " - " + activePlayer.getMana() + "\u27E1");
        System.out.println(activePlayer + " turn:");
//        System.out.println("MANA"+activePlayer.getMana());
        if (!activePlayer.getHand().isEmpty())
            System.out.println("  HIT: " + activePlayer.getHand().get(activePlayer.getHand().size()-1));
        else
            System.out.println("  EMPTY DECK... (-" + activePlayer.getPunishment() + " hp)");
        System.out.println("  HAND: " + activePlayer.getHand());
        System.out.println("  WARRIORS: " + activePlayer.getWarriors());
    }

    private void printInactivePlayerState() {
        System.out.println("  OPONENT WARRIORS: " + inactivePlayer.getWarriors());
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
            activePlayer.updateMana(move);
            activePlayer.hit();
            if (gameFinished())
                break;

            if (verbose) {
                printActivePlayerState();
                printInactivePlayerState();
            }
            List<Attack> selectedAttacks;
            List<Card> selectedCardsToPlay;
            if(activePlayer instanceof MCTSPlayer)
            {
                long startTime=System.currentTimeMillis();
                selectedAttacks = activePlayer.selectAttacksToPlay(inactivePlayer, activePlayer.getPossibleAttacks(inactivePlayer, move));
                selectedCardsToPlay = activePlayer.selectCardsToPlay(activePlayer.getPossibleCardsToPlay(inactivePlayer, move));
                long endTime=System.currentTimeMillis();
                decisionTimes.add(endTime-startTime);
            }
            else {
                selectedAttacks = activePlayer.selectAttacksToPlay(inactivePlayer, activePlayer.getPossibleAttacks(inactivePlayer, move));
                selectedCardsToPlay = activePlayer.selectCardsToPlay(activePlayer.getPossibleCardsToPlay(inactivePlayer, move));
            }
            activePlayer.attackOpponentsCards(inactivePlayer, selectedAttacks, verbose);
            activePlayer.playCards(selectedCardsToPlay, verbose);
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
