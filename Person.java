import javax.swing.JLabel;

public class Person {
    
    private Hand hand;
    private String name;

    public Person(){
        this.hand =  new Hand();
        this.name = "";
    }

    //Setters and Getters
    public Hand getHand(){
        return this.hand;
    }
    public void setHand(Hand hand){
        this.hand = hand;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public void printHand(JLabel[] cardPics){
   
    }
}