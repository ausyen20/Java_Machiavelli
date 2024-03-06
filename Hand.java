import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand;

    public Hand(){
        hand = new ArrayList<Card>();
    }

    public Hand(ArrayList<Card> hand){
        this.hand = hand;
    }

    public void takeCardfromDeck(Deck deck){
        hand.add(deck.takeCard());
    }

    public void takeCard2Hand(Card card){
        hand.add(card);
    }

    //Take Hand
    public void removeCardfromHand(Card card){
        hand.remove(card);
    }

    public void removeLastCard(){
        hand.remove(hand.size()-1);
    }
    public String toString(){
        String output = "";
        for(Card card: hand){
            output += card + " - ";
        }
        return output;
    }

    public Card getCard(int indx){
        return hand.get(indx);
    }

    public int getHandSize(){
        return hand.size();
    }

    //
    public ArrayList<Card> getHand(){
        return this.hand;
    }

 
}
