package mcts;

import game.Game;
import players.Player;

public class MCTS extends Game {

    private Node rootNode;

    public MCTS(Player activePlayer, Player inactivePlayer) {
        super(activePlayer, inactivePlayer);
        this.rootNode = new Node("0", null, activePlayer, inactivePlayer);
    }


    private Node selectChild() {
        return null;
    }

    private int extendUnexploredChild() {
        return 0;  // number of wonPlayouts (+ number of playouts to playedPlayouts)
    }

    private Player playoutSimulation() {
        return null;  // winner
    }

    private void backpropagateResults() {
        // update wonPlayouts and playedPlayouts
    }

}
