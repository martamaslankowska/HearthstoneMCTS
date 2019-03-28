package players;

import game.Card;

public class ControllingResult {
    Card target;
    int difference;

    public ControllingResult(Card target,int difference){
        this.target=target;
        this.difference=difference;
    }
}
