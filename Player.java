import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Player{

    private Hand playerHand;
    private String playerName;
    private int actionCounter;
    private int x, y;

    public Player(String playerName, int x, int y){
        this.playerHand =  new Hand();
        this.playerName = playerName;
        actionCounter = 0;
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public void addActionCounter(){
        actionCounter++;
    } 
    public int getActionCounter(){
        return actionCounter;
    }

    public void resetActionCounter(){
        actionCounter = 0;
    }

    public Hand getPlayerHand(){
       return this.playerHand;
    }
    public void setPlayerHand(Hand setPlayerHand){
        this.playerHand = setPlayerHand;
    }
    public String getPlayerName(){
        return this.playerName;
    }
    public void setPlayerName(String setPlayerName){
        this.playerName = setPlayerName;
    }

    public void printHand(ArrayList<JLabel> playerCards){
        System.out.println(this.playerHand.getHandSize());
        for(int i = 0; i < this.playerHand.getHandSize(); i++){
            playerCards.get(i).setVisible(false);
        }
        for(int i = 0; i < this.playerHand.getHandSize(); i++){
            String rank = this.playerHand.getCard(i).getRank().toString();
            String suit = this.playerHand.getCard(i).getSuit().toString();
            String fileName = rank+suit + ".png";
            playerCards.get(i).setIcon(new ImageIcon( new ImageIcon(Game.IMAGE_DIR+fileName).getImage().getScaledInstance(Game.CARD_WIDTH, Game.CARD_HEIGHT, Image.SCALE_SMOOTH)));
            playerCards.get(i).setVisible(true);
        }
    }

    //Not use
    public ArrayList<JLabel> newCardtoHand(Card card, ArrayList<JLabel> playerLabels){
        for(int i = 0; i < playerLabels.size(); i++){
            playerLabels.get(i).setVisible(false);
        }
        getPlayerHand().takeCard2Hand(card);

        playerLabels.clear();
        for(int i = 0; i < getPlayerHand().getHandSize(); i++){
            Card currCard = getPlayerHand().getCard(i);
            String fileName = currCard.getRank().toString() + currCard.getSuit().toString() + ".png"; 
            String cardName = currCard.getRank().toString() + currCard.getSuit().toString();

        }
        return null;
    }
    
    //Add a remove method from player's hand
}
