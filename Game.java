import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Game extends JPanel{
    //Constants
    public static final int CARD_WIDTH = 50;
    public static final int CARD_HEIGHT = 80;
    public static final String IMAGE_DIR = "img/";
    private int initialCardNumber = 3;

    //In-Game variable elements
    private int action_Counter = 0;
    private int draw_Counter = 0;
    //Frame Elements
    private JFrame jFrame;
    //In-Game standard variable
    private Deck deck;
    private Table table;
    private JButton endTurn, drawCard, dealCards;
    private boolean tableCombo;
    //Current Player
    private Player currPlayer;
    //Dealing: Selection
    private ArrayList<JLabel> selected = new ArrayList<JLabel>();
    private ArrayList<JLabel> selectedTable = new ArrayList<JLabel>();
    private ArrayList<ArrayList<JLabel>> selectedCombo = new ArrayList<ArrayList<JLabel>>();
    //All table combos
    private ArrayList<ArrayList<JLabel>> allTableCards = new ArrayList<ArrayList<JLabel>>();
    //players' cards
    private int playerNumbers;
    private Player player, player2, player3, player4;
    private ArrayList<Player> allPlayers = new ArrayList<Player>();
    private ArrayList<JLabel> playerCards = new ArrayList<JLabel>();
    private ArrayList<JLabel> player2Cards = new ArrayList<JLabel>();
    private ArrayList<JLabel> player3Cards = new ArrayList<JLabel>();
    private ArrayList<JLabel> player4Cards = new ArrayList<JLabel>();
    //Objects for selection
    private Object[] numberofPlayers = {"2", "4", "Exit"};

    public Game(JFrame jFrame){
        
        while(true){
            playerNumbers = selectNumberofPlayers(jFrame);
            break;
        }
        deck = new Deck(true);
        table = new Table();
        deck.shuffle();
        this.jFrame = jFrame;
        setupGUI();
        startRound(jFrame);
    }

    public int selectNumberofPlayers(JFrame frame){
        String s = (String) JOptionPane.showInputDialog(frame, "Please select number of players:\n", "Player Number Selection",
        JOptionPane.PLAIN_MESSAGE, null, numberofPlayers,"1"
        );
        int num = 0;
        if(s.equals("Exit")){
            String alert_info = "Exit Code: Game is closing";
            JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }else{
            num = Integer.valueOf(s);
        }
        return num;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#18320e"));
        g.fillRect(0,0,1000,800);
    }

    private void setupGUI(){
        this.setSize(1000, 1000);
        endTurn = new JButton("End Turn");
        endTurn.setBounds(0, 0, 110, 20);
        drawCard = new JButton("Draw a Card");
        drawCard.setBounds(0, 20, 110, 20);
        dealCards = new JButton("Deal");
        dealCards.setBounds(0, 40, 110, 20);

        this.setLayout(null);
        this.add(endTurn);
        this.add(drawCard);
        this.add(dealCards);
        /*
        ImageIcon testImage = new ImageIcon(IMAGE_DIR+"KingClubs.png");
        testImage = new ImageIcon(testImage.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        JLabel testCardLabel = new JLabel(testImage);
        testCardLabel.setBounds(10, 40, CARD_WIDTH, CARD_HEIGHT);
        this.add(testCardLabel);
        */

  
        initializePlayersHand(initialCardNumber, playerNumbers, deck);
        if(playerNumbers == 2){
            cardDownPlayer(player2Cards, player2);
        }else{
            cardDownPlayer(player2Cards, player2);
            cardDownPlayer(player3Cards, player3);
            cardDownPlayer(player4Cards, player4);
        }
        currPlayer = player;
    }
    //player 1 -> x = 240, y = 680 || player 2 -> x = 240, y = 0
    public void initializePlayersHand(int initialCardNumber, int numberofPlayers, Deck deck){
        if(numberofPlayers == 2){
            player = new Player("Player 1", 240, 680);
            player2 = new Player("Player 2", 240, 0);

            Hand hand1 = player.getPlayerHand();
            Hand hand2 = player2.getPlayerHand();
            int x = player.getX();
            int x2 = player2.getX();

            for(int i = 0; i < initialCardNumber; i++){
                hand1.takeCardfromDeck(deck);
                Card currentCard = hand1.getCard(i);
                String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();
                JLabel currJLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
                currJLabel.setBounds(x, player.getY(), CARD_WIDTH, CARD_HEIGHT);
                currJLabel.setName(cardName);
                this.add(currJLabel);
                playerCards.add(currJLabel);
                x += 30;
            }

            for(int i = 0; i < initialCardNumber; i++){
                hand2.takeCardfromDeck(deck);
                Card currentCard = hand1.getCard(i);
                String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();
                try {
                    BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR + fileName));
                    BufferedImage rotate_180 = rotate(original, 180.0d);
                    ImageIcon img = new ImageIcon(rotate_180.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(img);
                    label.setBounds(x2, player2.getY(), CARD_WIDTH, CARD_HEIGHT);
                    label.setName(cardName);
                    this.add(label);
                    player2Cards.add(label);
                    x2 += 30;
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
            allPlayers.add(player);
            allPlayers.add(player2);
        }else if(numberofPlayers == 4){
            player = new Player("Player 1", 240, 680); //South
            player2 = new Player("Player 2", 240, 0); //North
            player3 = new Player("Player 3", 0, 70); //West
            player4 = new Player("Player 4", 905, 230); //East
            allPlayers.add(player);
            allPlayers.add(player2);
            allPlayers.add(player3);
            allPlayers.add(player4);

            for(Player play : allPlayers){
                Hand hand = play.getPlayerHand();
                String name = play.getPlayerName();
                int x = play.getX();
                int y = play.getY();

                for(int i = 0; i < initialCardNumber; i++){
                    hand.takeCardfromDeck(deck);
                    Card currentCard = hand.getCard(i);
                    String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                    String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();
                    try {
                        BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR + fileName));
                        if(name.equals("Player 3") || name.equals("Player 4")){
                            BufferedImage rotate_90;
                            if(name.equals("Player 3")){
                                rotate_90 = rotate(original, 90.0d);
                            }else{
                                rotate_90 = rotate(original, -90.0d);
                            }
                            ImageIcon img = new ImageIcon(rotate_90.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
                            JLabel label = new JLabel(img);
                            label.setBounds(x, y, CARD_HEIGHT, CARD_WIDTH);
                            label.setName(cardName);
                            this.add(label);
                            if(name.equals("Player 3")){
                                player3Cards.add(label);
                            }else{
                                player4Cards.add(label);
                            }
                            y += 30;
                        }else{
                            ImageIcon img = new ImageIcon(original.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                            JLabel label = new JLabel(img);
                            label.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
                            label.setName(cardName);
                            this.add(label);
                            if(name.equals("Player 1")){
                                playerCards.add(label);
                            }else if(name.equals("Player 2")){
                                player2Cards.add(label);
                            }
                            x += 30;
                        }
                    }catch(Exception e) {
                        System.out.println("error 2");
                    }   
                }
            }
        }
    }

    public void cardUpPlayer(ArrayList<JLabel> arrJL, Player thisPlayer){
        Hand hand = thisPlayer.getPlayerHand();
        int x_axis = thisPlayer.getX();
        int y_axis = thisPlayer.getY();

        for(JLabel jl : arrJL){
            this.remove(jl);
        }
        arrJL.clear();

        if(thisPlayer.getPlayerName().equals("Player 4") && thisPlayer.getPlayerHand().getHandSize() > 14){
            y_axis -= 130;
        }

        String name = thisPlayer.getPlayerName();
        ArrayList<JLabel> ARR = new ArrayList<>();
        for(int i = 0; i < hand.getHandSize(); i++){
            Card currentCard = hand.getCard(i);
            String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
            String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();
            try {
                BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR + fileName));
                if(name.equals("Player 3") || name.equals("Player 4")){
                    BufferedImage rotate_90;
                    if(name.equals("Player 3")){
                        rotate_90 = rotate(original, 90.0d);
                    }else{
                        rotate_90 = rotate(original, -90.0d);
                    }
                    ImageIcon img = new ImageIcon(rotate_90.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(img);
                    label.setBounds(x_axis, y_axis, CARD_HEIGHT, CARD_WIDTH);
                    label.setName(cardName);
                    this.add(label);
                    ARR.add(label);
                    y_axis += 30;

                }else{
                    ImageIcon img = new ImageIcon(original.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(img);
                    label.setBounds(x_axis, y_axis, CARD_WIDTH, CARD_HEIGHT);
                    label.setName(cardName);
                    this.add(label);
                    ARR.add(label);
                    x_axis += 30;
                }
            } catch (Exception e) {
                System.out.println("error 3");

            }
        }
        arrJL.addAll(ARR);
    }

    public void cardDownPlayer(ArrayList<JLabel> arrJL, Player thisplayer){
        int x = thisplayer.getX();
        int y = thisplayer.getY();
        ArrayList<JLabel> jLs = new ArrayList<JLabel>();
        if(thisplayer.getPlayerName().equals("Player 4") && thisplayer.getPlayerHand().getHandSize() > 14){
            y -= 130;
        }

        for(JLabel jl : arrJL){
            String cardDown = "CardDown.png";
            String cardDownName = "CardDown";
            try {
                BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR + cardDown));
                ImageIcon img;
                if(thisplayer.getPlayerName().equals("Player 3")){
                    original = rotate(original, 90.0d);
                    img = new ImageIcon(original.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
                }else if(thisplayer.getPlayerName().equals("Player 4")){
                    original = rotate(original, -90.0d);
                    img = new ImageIcon(original.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
                }else{
                    img = new ImageIcon(original.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                }
                 
                JLabel label = new JLabel(img);
                if(thisplayer.getPlayerName().equals("Player 3") || thisplayer.getPlayerName().equals("Player 4")){
                    
                    label.setBounds(x, y, CARD_HEIGHT, CARD_WIDTH);
                }else{
                    label.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
                }
                label.setName(cardDownName);
                this.add(label);
                jLs.add(label);
            } catch (Exception e) {
                // TODO: handle exception
            }

            if(thisplayer.getPlayerName().equals("Player 3") || thisplayer.getPlayerName().equals("Player 4")){
                y += 30;
            }else {
                x += 30;
            }
            this.remove(jl);
        }
        arrJL.clear();
        arrJL.addAll(jLs);
    }

    //Game logics Below here
    public void decktoPlayer(Player player, Deck deck){
        Hand hand = player.getPlayerHand();
        int size = playerCards.size();

        JLabel lastElement = playerCards.get(size-1);
        int X = lastElement.getX();
        int Y = lastElement.getY();
        
        hand.takeCardfromDeck(deck);
        Card cardDeck = hand.getCard(size);
        String fileName = cardDeck.getRank().toString() + cardDeck.getSuit().toString() + ".png";
        String cardName = cardDeck.getRank().toString() + cardDeck.getSuit().toString();
        JLabel addJLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
        addJLabel.setBounds(X+30, Y, CARD_WIDTH, CARD_HEIGHT);
        addJLabel.setName(cardName);
        this.add(addJLabel);
        playerCards.add(addJLabel);
        System.out.println("Hand: " + hand.toString());
    }
    //Rotate ImgsIcon
    public ImageIcon rotateImageIcon(ImageIcon pic, double angle){
        int w = pic.getIconWidth();
        int h = pic.getIconHeight();
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage image = new BufferedImage(h, w, type);
        Graphics2D g2 = image.createGraphics();
        double x = (h - w)/2.0;
        double y = (w - h)/2.0;
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(angle, w/2.0, h/2.0);
        g2.drawImage(pic.getImage(), at, null);
        g2.dispose();
        pic = new ImageIcon(image);
        System.out.println(w + " , " + h);
        return pic;
    }
    
    //Rotate imgs
    public BufferedImage rotate(BufferedImage img, Double degrees){
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.round(img.getWidth() * cos + img.getHeight() * sin);
        int newHeight = (int) Math.round(img.getWidth() * sin + img.getHeight() * cos);
        BufferedImage rotate = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotate.createGraphics();
        int x = (newWidth - img.getWidth()) / 2;
        int y = (newHeight - img.getHeight()) / 2;

        AffineTransform at = new AffineTransform();
        at.setToRotation(radians, x + (img.getWidth() / 2), y + (img.getHeight() / 2));
        at.translate(x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return rotate;
    }
    
    public void switchPlayer(){

        if(playerNumbers == 2){
            if(currPlayer.getPlayerName().equals("Player 1")){
                Hand hand = currPlayer.getPlayerHand();
                player.setPlayerHand(hand);
                currPlayer = player2;
                cardDownPlayer(playerCards, player);
                cardUpPlayer(player2Cards, currPlayer);
                resetFrame();
            }else if(currPlayer.getPlayerName().equals("Player 2")){
                Hand hand = currPlayer.getPlayerHand();
                player2.setPlayerHand(hand);
                currPlayer = player;
                cardDownPlayer(player2Cards, player2);
                cardUpPlayer(playerCards, currPlayer);
                resetFrame();
            }
        }

        if(playerNumbers == 4){
            if(currPlayer.getPlayerName().equals("Player 1")){
                Hand hand = currPlayer.getPlayerHand();
                player.setPlayerHand(hand);
                currPlayer = player3;
                cardDownPlayer(playerCards, player);
                cardUpPlayer(player3Cards, currPlayer);
                resetFrame();
            }else if(currPlayer.getPlayerName().equals("Player 3")){
                Hand hand = currPlayer.getPlayerHand();
                player3.setPlayerHand(hand);
                currPlayer = player2;
                cardDownPlayer(player3Cards, player3);
                cardUpPlayer(player2Cards, currPlayer);
                resetFrame();
            }else if(currPlayer.getPlayerName().equals("Player 2")){
                Hand hand = currPlayer.getPlayerHand();
                player2.setPlayerHand(hand);
                currPlayer = player4;
                cardDownPlayer(player2Cards, player2);
                cardUpPlayer(player4Cards, currPlayer);
                resetFrame();
            }else if(currPlayer.getPlayerName().equals("Player 4")){
                Hand hand = currPlayer.getPlayerHand();
                player4.setPlayerHand(hand);
                currPlayer = player;
                cardDownPlayer(player4Cards, player4);
                cardUpPlayer(playerCards, currPlayer);
                resetFrame();
            }
        }
    }

    public void startRound(JFrame frame){
        // every row has 7 sets, each column has 5 sets, total 35 sets 
        //testadding(130, 100); //end at 250, 195

        endTurn.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(action_Counter == 0){
                    String alert_info = currPlayer.getPlayerName() + " has not yet play or drawn a card. Please perform either!";
                    JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    switchPlayer();
                    selected.clear();
                    selectedTable.clear();
                    selectedCombo.clear();
                    action_Counter = 0;
                    draw_Counter = 0;
                }   
            }
        });

        dealCards.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                boolean valid = false;
                System.out.println("select: " + selected.size() + ", table select: " + selectedTable.size() + ", bool: " + tableCombo);
                //Adding a single card into existing combo

                if(draw_Counter > 0){
                    String alert_info = currPlayer.getPlayerName() + " has already draw a card. You can't deal any card, please end turn!";
                    JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    if(selected.size() > 0 && table.getTableSize() >= 1 && tableCombo == true && selectedTable.size() == 1 && selectedCombo.size() == 1){
                        ArrayList<Card> cTemp = new ArrayList<Card>();
                        for(JLabel jl : selected){
                            Card addingCard = string2Card(jl.getName());
    
                            cTemp.add(addingCard);
                        }
                        boolean dealing = deal1ormore(cTemp, selectedCombo, selectedTable);

                        if(dealing == true){
                            removeCardFromPlayer(cTemp, currPlayer);
                            redisplayPlayerCards(currPlayer);
                            selected.clear();
                            selectedTable.clear();
                            selectedCombo.clear();
                            frame.pack();
                            frame.setSize(1000,800);
                            action_Counter++;
                            checkGameOver(currPlayer);
                        }else{
                            String alert_info = currPlayer.getPlayerName() + ": Illegal Dealings, Please re-try another combination!";
                            JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                        }
                    
                        System.out.println("Dealing single card , " + dealing);
                        return;
                    }
    
                    //For normal selections
                    if(selected.size() > 2 && tableCombo == false){
                        valid = validateSelected(selected);
                        if(valid == true){
                            System.out.println("Valid: " + valid);
                            ArrayList<Card> cards = new ArrayList<Card>();
                            for(int i = 0; i < selected.size(); i++){
                                Card currCard = string2Card(selected.get(i).getName());
                                //System.out.println("Curr: " + currCard.getRank().toString() + ", " + currCard.getSuit().toString());
                                cards.add(currCard);
                            }
                            placeCardsonTable(cards);
                            //Remove cards dependent on player (current)
                            removeCardFromPlayer(cards, currPlayer);
                            redisplayPlayerCards(currPlayer);
                            selected.clear();
                            frame.pack();
                            frame.setSize(1000,800);
                            action_Counter++;
                            checkGameOver(currPlayer);
                            return;
                        }else{
                            String alert_info = currPlayer.getPlayerName() + ": Illegal Combination, Please re-try another combination!";
                            JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        // ** draw cards to current player's hand
        drawCard.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){     
                
                if(deck.getDeckSize() == 0){
                    String alert_info = currPlayer.getPlayerName() + " has already performed an action, please end turn!";
                    JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }else if(draw_Counter == 0 && action_Counter == 0){
                    drawACard2Player(currPlayer);
                    //Helps readjust frame
                    selected.clear();
                    selectedTable.clear();
                    selectedCombo.clear();
                    frame.pack();
                    frame.setSize(1000,800);
                    draw_Counter++;
                    action_Counter++;
                }else if(draw_Counter == 0 && action_Counter > 0){
                    String alert_info = currPlayer.getPlayerName() + " has already performed an action, please end turn!";
                    JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    String alert_info = currPlayer.getPlayerName() + " has already drawn a card. Please end the turn!";
                    JOptionPane.showMessageDialog(null, alert_info, "Game Alert", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                System.out.println(e.getX() + " , " + e.getY());
                System.out.println("Selected: " + selected.size() + ", Table: " + selectedTable.size());
                System.out.println("Curr player: " + currPlayer.getPlayerName());
                if (e.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("right clicked");


                }else if(e.getButton() == MouseEvent.BUTTON1){
                    // ---- Below is handling player cards (up-down) or (left-right)
                    //South
                    if(currPlayer.getPlayerName().equals("Player 1") && !(playerCards.get(0).getName().equals("CardDown"))){
                        for (JLabel jL : playerCards){
                            Rectangle bd =  jL.getBounds();
                            if(bd.contains(e.getPoint())){
                                if(!(selected.contains(jL))){
                                    selected.add(jL);                               
                                    jL.setBounds(jL.getX(), jL.getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                    jFrame.pack();
                                    jFrame.setSize(1000, 800);
                                    return;
                                }else{
                                    selected.remove(jL);
                                    jL.setBounds(jL.getX(), jL.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                    resetFrame();
                                    return;
                                }
                            }
                        }
                        //West
                    }else if(currPlayer.getPlayerName().equals("Player 3") && !(player3Cards.get(0).getName().equals("CardDown"))){
                        for(JLabel jK : player3Cards){
                            Rectangle bd =  jK.getBounds();
                            if(bd.contains(e.getPoint())){
                                if(!(selected.contains(jK))){
                                    jK.setBounds(jK.getX() + 10, jK.getY(), CARD_HEIGHT, CARD_WIDTH);
                                    selected.add(jK);
                                    resetFrame();
                                    return;
                                }else{

                                    jK.setBounds(jK.getX() - 10, jK.getY(), CARD_HEIGHT, CARD_WIDTH);
                                    selected.remove(jK);
                                    resetFrame();
                                    return;
                                }
                            }
                        }
                    }else if(currPlayer.getPlayerName().equals("Player 2") && !(player2Cards.get(0).getName().equals("CardDown"))){
                        for(JLabel jH : player2Cards){
                            Rectangle bd =  jH.getBounds();
                            if(bd.contains(e.getPoint())){
                                if(!(selected.contains(jH))){
                                    jH.setBounds(jH.getX(), jH.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                    selected.add(jH);
                                    resetFrame();
                                    return;
                                }else{
                                    jH.setBounds(jH.getX(), jH.getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                    selected.remove(jH);
                                    resetFrame();
                                    return;
                                }
                            }
                        }
                    }else if(currPlayer.getPlayerName().equals("Player 4") && !(player4Cards.get(0).getName().equals("CardDown"))){
                        for(JLabel jG : player4Cards){
                            Rectangle bd =  jG.getBounds();
                            if(bd.contains(e.getPoint())){
                                if(!(selected.contains(jG))){
                                    jG.setBounds(jG.getX() - 10, jG.getY(), CARD_HEIGHT, CARD_WIDTH);
                                    selected.add(jG);
                                    resetFrame();
                                    return;
                                }else{
                                    jG.setBounds(jG.getX() + 10, jG.getY(), CARD_HEIGHT, CARD_WIDTH);
                                    selected.remove(jG);
                                    resetFrame();
                                    return;
                                }
                            }
                        }
                    }
                    // ---- Above is handling player cards (up-down) or (left-right)
                    for(ArrayList<JLabel> arrJL : allTableCards){
                        int max = arrJL.size() - 1;
                        for(int i = 0; i < arrJL.size(); i++ ){
                            Rectangle bd2 = arrJL.get(i).getBounds();
                            if(bd2.contains(e.getPoint())){
                                if(!(selectedTable.contains(arrJL.get(i))) && i == 0 && !(selectedTable.contains(arrJL.get(max)))){
                                    //selected.add(arrJL.get(i));
                                    if(selectedCombo.size() == 1){
                                        System.out.println("Over: " + selectedCombo.size());
                                        break;
                                    }
                                    selectedTable.add(arrJL.get(i));
                                    selectedCombo.add(arrJL);
                                    arrJL.get(i).setBounds(arrJL.get(i).getX(), arrJL.get(i).getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                    tableCombo = true;
                                    repaint();
                                
                                }else if(!(selectedTable.contains(arrJL.get(i))) && i == max && !(selectedTable.contains(arrJL.get(0))) ){
                                    //selected.add(arrJL.get(i));
                                    if(selectedCombo.size() == 1){
                                        System.out.println("Over: " + selectedCombo.size());
                                        break;
                                    }
                                    selectedTable.add(arrJL.get(i));
                                    selectedCombo.add(arrJL);
                                    arrJL.get(i).setBounds(arrJL.get(i).getX(), arrJL.get(i).getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                    tableCombo = true;
                                    repaint();
                                }else if(selectedTable.contains(arrJL.get(i)) && i == 0){
                                    //selected.remove(arrJL.get(i));
                                    selectedTable.remove(arrJL.get(i));
                                    selectedCombo.remove(arrJL);
                                    arrJL.get(i).setBounds(arrJL.get(i).getX(), arrJL.get(i).getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                    tableCombo = false;
                                    repaint();
                                }
                                else if(selectedTable.contains(arrJL.get(i)) && i == max){
                                    //selected.remove(arrJL.get(i));
                                    selectedTable.remove(arrJL.get(i));
                                    selectedCombo.remove(arrJL);
                                    arrJL.get(i).setBounds(arrJL.get(i).getX(), arrJL.get(i).getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                    tableCombo = false;
                                    repaint();
                                }
                                System.out.println("Select Table combos: " + tableCombo + "\nTable count: " + table.getTableSize() + ", Select Combos: " + selectedCombo.size());
                                return;
                            }
                        }

                    }
                }
                // *2* --> Changes are below
                // Only allow the side cards on table can be selected -> t
            }
        });
    }

    public void resetFrame(){
        jFrame.pack();
        jFrame.setSize(1000,800);
    }

    public void printallTable(){
       int count = 0;
        for(ArrayList<JLabel> jl : allTableCards){
            System.out.println("Count: " + count);
            for(JLabel L : jl){
                System.out.println("Card: " + string2Card(L.getName()));
            }
            count++;
       }
    }
    
    //Redisplay player's card based on given player's hand
    public void redisplayPlayerCards(Player curplayer){
        int x = 0;
        if(curplayer.getPlayerName() == "Player 1" || curplayer.getPlayerName() == "Player 2"){
            if(curplayer.getPlayerHand().getHandSize() > 14){
                x = 180;
            }else{
                x = 240;
            }

            if(curplayer.getPlayerName() == "Player 1" ){
                for(int i = 0; i < playerCards.size(); i++){
                    playerCards.get(i).setVisible(false);
                    removeLabel(playerCards.get(i));
                }
                playerCards.clear();
            }else if(curplayer.getPlayerName() == "Player 2"){
                for(int i = 0; i < player2Cards.size(); i++){
                    player2Cards.get(i).setVisible(false);
                    removeLabel(player2Cards.get(i));
                }
                player2Cards.clear();
            }
            
            for(int i = 0; i < curplayer.getPlayerHand().getHandSize(); i++){
                Card currentCard = curplayer.getPlayerHand().getCard(i);
                String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();
                JLabel currJLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));

                
                if(curplayer.getPlayerName() == "Player 2"){
                    currJLabel.setBounds(x, 0, CARD_WIDTH, CARD_HEIGHT);
                }else{
                    currJLabel.setBounds(x, 680, CARD_WIDTH, CARD_HEIGHT);
                }
                currJLabel.setName(cardName);
                this.add(currJLabel);

                if(curplayer.getPlayerName() == "Player 1" ){
                    playerCards.add(currJLabel);
                }else if(curplayer.getPlayerName() == "Player 2" ){
                    player2Cards.add(currJLabel);
                }
                x += 30;
            }
        }
        int y_ax = 0;
        if(curplayer.getPlayerName() == "Player 3" || curplayer.getPlayerName() == "Player 4"){
            y_ax = curplayer.getY();

            if(curplayer.getPlayerName() == "Player 3"){
                for(int i = 0; i < player3Cards.size(); i++){
                    player3Cards.get(i).setVisible(false);
                    removeLabel(player3Cards.get(i));
                }
                player3Cards.clear();
            }else if(curplayer.getPlayerName() == "Player 4"){
                for(int i = 0; i < player4Cards.size(); i++){
                    player4Cards.get(i).setVisible(false);
                    removeLabel(player4Cards.get(i));
                }
                player4Cards.clear();

                if(curplayer.getPlayerHand().getHandSize() > 14){
                    y_ax -= 130;
                }
            }

            for(int i = 0; i < curplayer.getPlayerHand().getHandSize(); i++){
                Card currentCard = curplayer.getPlayerHand().getCard(i);
                String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();

                try {
                    BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR + fileName));
                    BufferedImage rotate_90 = null;
                    if(curplayer.getPlayerName() == "Player 3"){
                        rotate_90 = rotate(original, 90.0d);
                    }else if(curplayer.getPlayerName() == "Player 4"){
                        rotate_90 = rotate(original, -90.0d);
                    }

                    ImageIcon img = new ImageIcon(rotate_90.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(img);
                    label.setBounds(curplayer.getX(), y_ax, CARD_HEIGHT, CARD_WIDTH);
                    label.setName(cardName);
                    this.add(label);
                    if(curplayer.getPlayerName() == "Player 3"){
                        player3Cards.add(label);
                    }else if(curplayer.getPlayerName() == "Player 4"){
                        player4Cards.add(label);
                    }
                    y_ax += 30;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
    }

    public void checkGameOver(Player currentPlayer){
        if(currentPlayer.getPlayerHand().getHandSize() == 0){
            String alert_info = currPlayer.getPlayerName() + " emptied the hand! Game Over!";
            JOptionPane.showMessageDialog(null, alert_info, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    //Draw a card to current player's hand
    public void drawACard2Player(Player currplayer){
        //player 1 = 240, 680
        //player 2 = 240, 0
        //player 3 = 0, 70
        //player 4 = 905, 270

        int x = 0;
        //Card card = deck.takeCard();
        System.out.println("Hand: " + currplayer.getPlayerHand().getHandSize());
        if(currplayer.getPlayerName() == "Player 1"){
            if(currplayer.getPlayerHand().getHandSize() > 14){
                x = 180;
            }else{
                x = 240;
            }
            for(int i = 0; i<playerCards.size(); i++){
                playerCards.get(i).setVisible(false);
                removeLabel(playerCards.get(i));
            }
            playerCards.clear();
            //Take card
            //currplayer.getPlayerHand().takeCard2Hand(card);
            currplayer.getPlayerHand().takeCardfromDeck(deck);
            
            for(int i = 0; i < currplayer.getPlayerHand().getHandSize(); i++){
                Card currentCard = currplayer.getPlayerHand().getCard(i);
                String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();

                JLabel currJLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
                currJLabel.setBounds(x, currplayer.getY(), CARD_WIDTH, CARD_HEIGHT);
                currJLabel.setName(cardName);
                this.add(currJLabel);
                playerCards.add(currJLabel);
                x += 30;
            }
        }else if(currplayer.getPlayerName() == "Player 2"){
            if(currplayer.getPlayerHand().getHandSize() > 14){
                x = 180;
            }else{
                x = 240;
            }
            for(int i = 0; i < player2Cards.size(); i++){
                player2Cards.get(i).setVisible(false);
                removeLabel(player2Cards.get(i));
            }
            player2Cards.clear();
            currplayer.getPlayerHand().takeCardfromDeck(deck);

            for(int i = 0; i < currplayer.getPlayerHand().getHandSize(); i++){
                Card currentCard = currplayer.getPlayerHand().getCard(i);
                String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();

                JLabel currJLabel = new JLabel(new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
                currJLabel.setBounds(x, currplayer.getY(), CARD_WIDTH, CARD_HEIGHT);
                currJLabel.setName(cardName);
                this.add(currJLabel);
                player2Cards.add(currJLabel);
                x += 30;
            }
        }
        int y = 0;
        if(currplayer.getPlayerName() == "Player 3" || currplayer.getPlayerName() == "Player 4" ){
            if(currplayer.getPlayerName() == "Player 3"){
                for(int i = 0; i < player3Cards.size(); i++){
                    player3Cards.get(i).setVisible(false);
                    removeLabel(player3Cards.get(i));
                }
                player3Cards.clear();
            }else if(currplayer.getPlayerName() == "Player 4"){
                for(int i = 0; i < player4Cards.size(); i++){
                    player4Cards.get(i).setVisible(false);
                    removeLabel(player4Cards.get(i));
                }
                player4Cards.clear();
            }

            currplayer.getPlayerHand().takeCardfromDeck(deck);
            x = currplayer.getX();
            y = currplayer.getY();
            if(currplayer.getPlayerHand().getHandSize() > 14 && currplayer.getPlayerName() == "Player 4"){
               y -= 170;
            }

            try {
                for(int i = 0; i < currplayer.getPlayerHand().getHandSize(); i++){
                    Card currentCard = currplayer.getPlayerHand().getCard(i);
                    String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
                    String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();

                    BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR+fileName));
                    BufferedImage rotate_90 = null;
                    if(currplayer.getPlayerName() == "Player 3"){
                        rotate_90 = rotate(original, 90.0d);
                    }else if(currplayer.getPlayerName() == "Player 4"){
                        rotate_90 = rotate(original, -90.0d);
                    }
                    
                    ImageIcon img =  new ImageIcon(rotate_90.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
                    JLabel label = new JLabel(img);
                    label.setBounds(x, y, CARD_HEIGHT, CARD_WIDTH);
                    label.setName(cardName);
                    this.add(label);
                    
                    if(currplayer.getPlayerName() == "Player 3"){
                        player3Cards.add(label);
                        y += 30;
                    }else if(currplayer.getPlayerName() == "Player 4"){
                        player4Cards.add(label);
                        y += 30;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error at Draw card");
            }
        }
    }

    //Place cards on tables *(Need to sort if there an Ace)
    int count = 1;
    int x = 120;
    int y = 120;
    int z = 0;
    public void placeCardsonTable(ArrayList<Card> cardsBePlaced){
        /*
        //Starting coord x = 120, y = 120
        //Each row ends at x = 820, y = 120 (each gap += 100) - can only fit 8 combos 
        //Each column ends at y = 560 (each gap += 110) - can only fit 5 combos
        */
   
        //Sort cards by rank
        Collections.sort(cardsBePlaced, new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2){
                return card1.getRank().compareTo(card2.getRank());
            }
        });

        System.out.println("To be Placed: " + cardsBePlaced);
        boolean findAce = false;
        if(cardsBePlaced.get(0).getRank().equals(Rank.ACE)){
            findAce = true;
        }
        
        if(findAce == true){
            if(cardsBePlaced.get(cardsBePlaced.size()-1).getRank().equals(Rank.KING)){
                Card ace_to_last = cardsBePlaced.remove(0);
                cardsBePlaced.add(ace_to_last);
            }
        }

        table.addSelected2Table(cardsBePlaced);
        
        // *1*
        ArrayList<Card> getLast = table.getLastFromTable();
        System.out.println("Get last: " + getLast.toString() );
        ArrayList<JLabel> addingLabels = new ArrayList<JLabel>();
        boolean moreThan5 = false;
        if(getLast.size() > 5){ 
            moreThan5 = true;
        }
        if(count % 8 == 0){
            y += 110;
            x = 120;
            z = 0;
        }
        for(int i = 0; i < getLast.size(); i++){
            Card card = getLast.get(i);
            String fileName = card.getRank().toString() +  card.getSuit().toString() + ".png";
            String cardName = card.getRank().toString() +  card.getSuit().toString();
            JLabel currLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
            currLabel.setBounds(x+z, y, CARD_WIDTH, CARD_HEIGHT);
            currLabel.setName(cardName);
            this.add(currLabel);
            addingLabels.add(currLabel);
            z += 10;

            if(i == 2 && moreThan5 == true){
                Card last2 = getLast.get(getLast.size()-2);
                Card last1 = getLast.get(getLast.size()-1);
                String fN2 = last2.getRank().toString() +  last2.getSuit().toString() + ".png";
                String cN2 = last2.getRank().toString() +  last2.getSuit().toString();
                String fN1 = last1.getRank().toString() +  last1.getSuit().toString() + ".png";
                String cN1 = last1.getRank().toString() +  last1.getSuit().toString();
                JLabel lb2 = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fN2).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
                JLabel lb1 = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fN1).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));

                lb2.setBounds(x+z, y, CARD_WIDTH, CARD_HEIGHT);
                lb2.setName(cN2);
                z += 10;
                lb1.setBounds(x+z, y, CARD_WIDTH, CARD_HEIGHT);
                lb1.setName(cN1);
                z += 10;
                
                this.add(lb2);
                this.add(lb1);
                addingLabels.add(lb2);
                addingLabels.add(lb1);
                break;
            }
        }
        count++;
        x += 70;
        System.out.println("X: " + x + ", y: " + y + ", z: " + z + ", Count: " + count);
        allTableCards.add(addingLabels);
        System.out.print("Table: " + table.getTableSize() + " CombO: " + allTableCards.size());
    }

    // => card to be placed

    //Set player's hand and remove the cards
    public void removeCardFromPlayer(ArrayList<Card> cardAlreadyPlaced , Player currPlayer){
        ArrayList<Card> playerCards = currPlayer.getPlayerHand().getHand();
        Iterator<Card> itr = playerCards.iterator();
        while(itr.hasNext()){
            Card c = itr.next();
            for(int i = 0; i < cardAlreadyPlaced.size(); i++){
                Card c2 = cardAlreadyPlaced.get(i);
                if(c.getRank().rankValue == c2.getRank().getRankValue() && c.getSuit() == c2.getSuit()){
                    itr.remove();
                    break;
                }
            }
        }
        Hand newHand = new Hand(playerCards);
        currPlayer.setPlayerHand(newHand);
        //System.out.println("New hand: " + currPlayer.getPlayerHand());
        //System.out.println("--> " + playerCards);
    }

    //--------------------
    public void testadding(int xs, int ys){
        try{
            int x = xs;
            int y = ys;
            BufferedImage img1 = ImageIO.read(getClass().getResource(IMAGE_DIR+"AceClubs.png"));
            BufferedImage img2 = ImageIO.read(getClass().getResource(IMAGE_DIR+"AceDiamonds.png")); 
            BufferedImage img3 = ImageIO.read(getClass().getResource(IMAGE_DIR+"AceHearts.png"));
            BufferedImage img4 = ImageIO.read(getClass().getResource(IMAGE_DIR+"AceSpades.png"));
            BufferedImage img5 = ImageIO.read(getClass().getResource(IMAGE_DIR+"TwoSpades.png"));
            ImageIcon ic1 = new ImageIcon(img1.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb1 = new JLabel(ic1);
            jb1.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
            ImageIcon ic2 = new ImageIcon(img2.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb2 = new JLabel(ic2);
            jb2.setBounds(x+10, y-10, CARD_WIDTH, CARD_HEIGHT);
            ImageIcon ic3 = new ImageIcon(img3.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb3 = new JLabel(ic3);
            jb3.setBounds(x+20, y, CARD_WIDTH, CARD_HEIGHT);
            ImageIcon ic4 = new ImageIcon(img4.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb4 = new JLabel(ic4);
            jb4.setBounds(x+30, y, CARD_WIDTH, CARD_HEIGHT);
            ImageIcon ic5 = new ImageIcon(img5.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb5 = new JLabel(ic5);
            jb5.setBounds(x+40, y, CARD_WIDTH, CARD_HEIGHT);


            this.add(jb1);
            this.add(jb2);
            this.add(jb3);
            this.add(jb4);
            this.add(jb5);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    //----------------------------------

    //Game Rules
    //This may need to be a string method
    public boolean validateSelected(ArrayList<JLabel> list){
        ArrayList<String> currentCards = new ArrayList<>();
        ArrayList<Card> allcards = new ArrayList<Card>();

        for(JLabel jl : list){
            String adding = jl.getName();
            currentCards.add(adding);
            Card card = string2Card(adding);
            allcards.add(card);
        }

        boolean ifValid = checkStraight(allcards);
        boolean ifValid2 = findSuitStraight(allcards);
        //System.out.println("Valid: " + ifValid);
        //System.out.println("Valid2: " + ifValid2);

        if(ifValid == true && ifValid2 == false){
            return true;
        }else if(ifValid == false && ifValid2 == true){
            return true;
        }
        return false;
    }
    
    //Deal one or more dependent on the existing combos on the table.
    public boolean deal1ormore(ArrayList<Card> selectedHands, ArrayList<ArrayList<JLabel>> selectedCombo, ArrayList<JLabel> LeftorRight){
        ArrayList<JLabel> currCombo = selectedCombo.get(0);
        ArrayList<Card> temp = new ArrayList<Card>();
        
        for(JLabel jl : currCombo){
            String name = jl.getName();
            Card card = string2Card(name);
            temp.add(card);
        }

        System.out.println("Cards: " + temp.toString() + " || Hand: " + selectedHands.toString() + "\n Size: " + currCombo.size());
        
        boolean left_right = false;
        boolean valid = false;
        if(currCombo.get(0).equals(LeftorRight.get(0))){
            System.out.println("    Left");
            boolean left = check_Dealing(selectedHands, temp, left_right);
            if(left == false){
                return false;
            }else{
                valid = true;
            }

        }else if(currCombo.get(currCombo.size()-1).equals(LeftorRight.get(0))){
            System.out.println("    Right");
            left_right = true;
            boolean right = check_Dealing(selectedHands, temp, left_right);
            if(right == false){
                return false;
            }else{
                valid = true;
            }
        }
        //If the checks pass, then 
        if(valid == true){
            ArrayList<Card> checkforAce = new ArrayList<Card>();
            int AceCount = 0;
            checkforAce.addAll(selectedHands);
            checkforAce.addAll(temp);

            for(Card i : checkforAce){
                if(i.getRank().equals(Rank.ACE)){
                    AceCount += 1;
                }
                if(AceCount > 1){
                    System.out.println("more than 1 Ace");
                    return false;
                }
            }
            
            System.out.println("Curr Combo (1): " + currCombo.get(0).getBounds());

            placeCards_from_Dealing(checkforAce, currCombo, temp, left_right);

            
            return true;
        }

        return false;
    }
    public void placeCards_from_Dealing(ArrayList<Card> dealCards, ArrayList<JLabel> currCombo, ArrayList<Card> tableCombo, boolean left_right){
        int xs = currCombo.get(0).getX();
        int ys = currCombo.get(0).getY();
        
        if(left_right == false){
            JLabel temp = currCombo.get(currCombo.size()-1);
            ys = temp.getY();
        }else{
            JLabel temp = currCombo.get(0);
            ys = temp.getY();
        }

        boolean isAce = false;
        Collections.sort(dealCards, new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2){
                return card1.getRank().compareTo(card2.getRank());
            }
        });

        for(Card i : dealCards){
            if(i.getRank().equals(Rank.ACE)){
                isAce = true;
            }
        }
        if(isAce == true){
            if(dealCards.get(dealCards.size()-1).getRank().equals(Rank.KING)){
                Card ace_to_last = dealCards.remove(0);
                dealCards.add(ace_to_last);
            }
        }
        int index = 0;
        for(ArrayList<JLabel> jlarr: allTableCards){
            if(jlarr.equals(selectedCombo.get(0))){
                System.out.println("Found");
                break;
            }
            index += 1;
        }

        ArrayList<ArrayList<Card>> allcards = table.getTable();
        ArrayList<JLabel> allcardJLs = allTableCards.get(index);

        //Replace the dealing card array via the index 
        System.out.println("Before Table : "  + allcards + "\nIndex: " + index);
        table.replaceListviaIndex(dealCards, index);
        System.out.println("After Table : " +  allcards);

        boolean moreThan5 = false;
        if(dealCards.size() > 5){
            moreThan5 = true;
            System.out.println("More than 5");
        }
        System.out.println("X: " + xs + ", Y: "+ ys);
        
        for(JLabel i : allcardJLs){
            System.out.println("cr: " + i.getName());

        }
        ArrayList<JLabel> arrJLs = new ArrayList<JLabel>();
        int zx = 0;
        //This cause the cards to go up by 10
        for(JLabel jl: currCombo){
            System.out.println("Curr: " + jl.getName() + ", BD: " + jl.getBounds());
            if(jl.equals(selectedTable.get(0))){
                System.out.println("Found");
                //jl.setBounds(jl.getX(), jl.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
            }
            removeLabel(jl);
        }

        for(JLabel jl : selected){
            System.out.println("Curr: " + jl.getName() + ", BD: " + jl.getBounds());
            jl.setBounds(jl.getX(), jl.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
        }

        for(int i = 0; i < dealCards.size(); i++){
            Card card = dealCards.get(i);
            String fileName = card.getRank().toString() + card.getSuit().toString() + ".png";
            String cardName = card.getRank().toString() + card.getSuit().toString();
            JLabel jL = new JLabel(new ImageIcon( new ImageIcon(IMAGE_DIR+fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
            jL.setBounds(xs + zx, ys, CARD_WIDTH, CARD_HEIGHT);
            jL.setName(cardName);
            arrJLs.add(jL);
            this.add(jL);
            zx += 10;
            if(ys != 120){
                System.out.println("Changed 1");
            }
            if(i == 2 && moreThan5 == true){
                int size = dealCards.size() - 1;
                Card last2 = dealCards.get(size - 1);
                Card last1 = dealCards.get(size);
                String fN2 = last2.getRank().toString() + last2.getSuit().toString() + ".png";
                String fN1 = last1.getRank().toString() + last1.getSuit().toString() + ".png";
                String cN2 = last2.getRank().toString() + last2.getSuit().toString();
                String cN1 = last1.getRank().toString() + last1.getSuit().toString();
                JLabel jl2 = new JLabel(new ImageIcon( new ImageIcon(IMAGE_DIR+fN2).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
                JLabel jl1 = new JLabel(new ImageIcon( new ImageIcon(IMAGE_DIR+fN1).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));

                jl2.setBounds(xs + zx, ys, CARD_WIDTH, CARD_HEIGHT);
                jl2.setName(cN2);
                zx += 10;
                jl1.setBounds(xs + zx, ys, CARD_WIDTH, CARD_HEIGHT);
                jl1.setName(cN1);
                zx += 10;

                this.add(jl2);
                this.add(jl1);
                arrJLs.add(jl2);
                arrJLs.add(jl1);

                if(ys != 120){
                    System.out.println("Changed 2");
                }
                break;
            }
        }
        allTableCards.set(index, arrJLs);

        for(JLabel jl : arrJLs){
            System.out.println(" JL: " + jl.getName() + ", BD: " + jl.getBounds());
        }

        for(ArrayList<JLabel> arr : allTableCards){
            for(JLabel jl : arr){
                System.out.println("--> JL " + jl.getName() + ", BD: " + jl.getBounds());
            }
            System.out.println();
        }       
        //System.out.println("Table: " + table.getTable() + ", \n" + tableCombo + ", \n" + currCombo);
        //System.out.println("Deal cards: " + dealCards + ", BooL: " + isAce + ", INdex: " + count);
    }

    public boolean check_Dealing(ArrayList<Card> selectedHands, ArrayList<Card> currCombo, boolean left_right){
        //Check first two cards on the left
        if(left_right == false){
            Card first = currCombo.get(0);
            Card second = currCombo.get(1);
            ArrayList<Card> temp = new ArrayList<Card>();
            temp.add(first);
            temp.add(second);
            temp.addAll(selectedHands);

            boolean straight = checkStraight(temp);
            boolean suitStraight = findSuitStraight(temp);

            if(straight == true && suitStraight == false){
                System.out.println("1. S: " + straight + ", D: " + suitStraight);
                return true;
            }else if(straight == false && suitStraight == true){
                System.out.println("2. S: " + straight + ", D: " + suitStraight);
                return true;
            }else{
                return false;
            }
        }
        //Check last two cards on the right 
        else{
            int size = currCombo.size()-1;
            Card last = currCombo.get(size);
            Card secondLast = currCombo.get(size-1);
            ArrayList<Card> temp =  new ArrayList<Card>();
            temp.add(secondLast);
            temp.add(last);
            temp.addAll(selectedHands);

            boolean straight = checkStraight(temp);
            boolean suitStraight = findSuitStraight(temp);
            if(straight == true && suitStraight == false){
                System.out.println("3. S: " + straight + ", D: " + suitStraight);
                return true;
            }else if(straight == false && suitStraight == true){
                System.out.println("4. S: " + straight + ", D: " + suitStraight);
                return true;
            }else{
                return false;
            }
        }
    }
    //** 3 ** 

    //*** A straight of at least three consecutive cards of the same suit
    public boolean checkStraight(ArrayList<Card> cards){
        boolean ifAceisFound = false;
        boolean moreThan4 = false;
        if(cards.size() < 3){
            System.out.println("Illegal combo");
            //return "not";
            return false;
        }
        //Initial 1-13
        //Arr: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
        ArrayList<Integer> arr = new ArrayList<Integer>();
        int count = 1;
        for(int i = 0; i < 13; i++){
            arr.add(count);
            count += 1;
        }
        
        //AllEquals remain true if suits are the same
        for(Card c : cards){
            if(!c.getSuit().equals(cards.get(0).getSuit())){
                //System.out.println("Nt the same");
                //return "not same suits";
                return false;
            }

            if(c.getRank() == Rank.ACE){
                System.out.println("Ace found");
                ifAceisFound = true;
            }
        }

        //Sort cards by rank orders
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2){
                return card1.getRank().compareTo(card2.getRank());
            }
        });

        //If there are only 3 selected cards, check sequence
        if(cards.size() == 3){
            int lowNum = cards.get(0).getRank().getRankValue();
            int highNum = cards.get(2).getRank().getRankValue();
            int indexofLow = 0;
            int indexofHigh = 0;
        
            for(int i : arr){
                if(lowNum == i){
                    indexofLow = arr.indexOf(i);
                    //System.out.println(indexofLow + " , " + arr.get(indexofLow));
                }
                if(highNum == i){
                    indexofHigh = arr.indexOf(i);
                    //System.out.println(indexofHigh + " , " + arr.get(indexofHigh));
                }
            }
            indexofLow += 1;
            indexofHigh -= 1;
            if(ifAceisFound == true){
                Rank r1 = cards.get(1).getRank();
                Rank r2 = cards.get(2).getRank();
                if(cards.get(1).getRank() == Rank.QUEEN && cards.get(2).getRank() == Rank.KING){
                    System.out.println("Special: " + cards.get(0).getRank().getRankValue() + ", " + r1.getRankValue() + " , " + r2.getRankValue());
                    //return "QKA";
                    return true;
                }else if(r1 == Rank.TWO && r2 == Rank.THREE){
                    System.out.println("2 Special: " + cards.get(0).getRank().getRankValue() + ", " + r1.getRankValue() + " , " + r2.getRankValue());
                    //return "A23";
                    return true;
                }else{
                    //return "not ace";
                    return false;
                }
            }else if(indexofHigh == indexofLow){
                //System.out.println("Sequence");
                //return "seq";
                return true;
            }else{
                //System.out.println("Not Sequence");
                //return "not seq";
                return false;
            }
        }
        //When selected cards have more than 3, then go into this condition
        if(cards.size() > 3){
            System.out.println("More than 3");
            moreThan4 = true;
            System.out.println("Cards: " + cards);
            //Checking normal 3+ card straight
            boolean ifSequential = true;
            if(ifAceisFound == false){
                for(int i = 0; i < cards.size()-1; i++){
                    int x = cards.get(i).getRank().getRankValue();
                    int y = cards.get(i+1).getRank().getRankValue();
                    if(x != y -1){
                        ifSequential = false;
                        //return "not seq4";
                        return false;
                    }
                    //System.out.println("X: " + x + ", Y: " + y);
                }
            }
            //If there is Ace in selected
            boolean aceHands = true;
            System.out.println("Have Ace" + cards.get(cards.size()-1) + " , R: " + cards.get(cards.size() -1).getRank().getRankValue());
            if(cards.get(0).getRank() == Rank.ACE){
                
                if(cards.get(1).getRank() == Rank.TWO){
                    for(int y = 1 ; y < cards.size() -1 ; y++){
                        int num1 = cards.get(y).getRank().getRankValue();
                        int num2 = cards.get(y+1).getRank().getRankValue();
                        if(num1 != num2 - 1){
                            aceHands = false;
                            //return "not seq5";
                            return false;
                        }
                    }
                }else if(cards.get(cards.size()-1).getRank() == Rank.KING){
                    System.out.println("King at last");
                    for(int x = cards.size()-1; x >= 2; x--){
                        System.out.println("Second ChecK: " + cards.get(x).getRank().getRankValue());
                        int num2 = cards.get(x).getRank().getRankValue();
                        int num1 = cards.get(x-1).getRank().getRankValue();

                        System.out.println("Nums " + num2 + ", next: "+ num1);

                        if(num2 != num1+1){
                            aceHands = false;
                            //return "not seq6";
                            System.out.println("Not seq7");
                            return false;
                        }
                    }
                }
            }
            //Adjust this part so it returns the right condition
            if(ifAceisFound == true && aceHands == true && moreThan4 == true){
                System.out.println("Ace Hand is eq");
                //return "Ace eq";
                return true;
            }else if (ifAceisFound == true && aceHands == false && moreThan4 == true){
                System.out.println("Ace Hand is not eq");
                //return "Ace not eq";
                return false;
            }else if (ifAceisFound == false && ifSequential == false && moreThan4 == true){
                System.out.println("Normal hand is not eq");
                //return "Normal not";
                return false;
            }else if(ifAceisFound == false && ifSequential == true && moreThan4 == true){
                System.out.println("Normal hand is eq");
                //return "Normal eq";
                return true;
            }
        }
        //System.out.println("Cards: " + cards);
        System.out.println("End");
        return false;
    }

    public Boolean findSuitStraight(ArrayList<Card> cards){
        if(cards.size() < 3){
            //return "not";
            return false;
        }
        //Check if the cards are the same ranks
        for(int i = 0; i < cards.size()-1; i++){
            int rank1 = cards.get(i).getRank().getRankValue();
            int rank2 = cards.get(i+1).getRank().getRankValue();
            if(rank1 != rank2){
                //System.out.println("Rank not the same");
                //return "rank diff";
                return false;
            }
        }
        //Check if the cards have different suits, not identical!
        int cCount = 0;
        int dCount = 0;
        int sCount = 0;
        int hCount = 0;
        for(int j = 0; j<cards.size(); j++){
            Suit suit = cards.get(j).getSuit();
           
            for(Suit matchSuit: Suit.values()){
                if(suit.equals(matchSuit) && matchSuit == Suit.CLUB){
                    cCount += 1;
                }else if(suit.equals(matchSuit) && matchSuit == Suit.DIAMOND){
                    dCount += 1;
                }else if(suit.equals(matchSuit) && matchSuit == Suit.SPADE){
                    sCount += 1;
                }else if(suit.equals(matchSuit) && matchSuit == Suit.HEART){
                    hCount += 1;
                }
            }
        }
        if(cCount > 1 || dCount > 1 || sCount > 1 || hCount > 1){
            System.out.println("Duplicate Suits");
            //return "not";
            return false;
        }else{
            System.out.println("Good");
            //return "diffsuit_straight";
            return true;
        }
    }
    // Find label name and converts to Card 
    public Card string2Card(String name){
        Suit cSuit = null;
        Rank cRank = null;
        for(Rank rank: Rank.values()){
            String toFind = rank.toString();
            if(name.contains(toFind)){
                cRank = rank;
                break;
            }
        }
        for(Suit suit : Suit.values()){
            String toFind = suit.toString();
            if(name.contains(toFind)){
                cSuit = suit;
                break;
            }
        }
        //System.out.println("Rank: " + cRank.toString() + ", Suit: " + cSuit.toString());
        Card cCard = new Card(cSuit, cRank);
        return cCard;
    }

    public void addLabel(JLabel label){
        this.add(label);
    }
    public void removeLabel(JLabel label){
        this.remove(label);
    }
}

//Pure testing of rotation:
/*
        
        try{
            BufferedImage original = ImageIO.read(getClass().getResource(IMAGE_DIR+"AceClubs.png"));
            BufferedImage rotate180 = rotate(original, 180.0d);
            BufferedImage rotate1_180 = rotate(original, 180.0d);
            BufferedImage rotate_90 = rotate(original, 90.0d);
            BufferedImage rotateOpp_90 = rotate(original, -90.0d);

            ImageIcon img = new ImageIcon(original.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb1 = new JLabel(img);
            ImageIcon img2 = new ImageIcon(rotate180.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb2 = new JLabel(img2);
            ImageIcon img3 = new ImageIcon(rotate1_180.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            JLabel jb3 = new JLabel(img3);

            ImageIcon img4 = new ImageIcon(rotate_90.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
            JLabel jb4 = new JLabel(img4);

            ImageIcon img5 = new ImageIcon(rotateOpp_90.getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH));
            JLabel jb5 = new JLabel(img5);

            //jb1.setBounds(260, 0, CARD_WIDTH, CARD_HEIGHT);
            // 60 95
            jb2.setBounds(240, 0, CARD_WIDTH, CARD_HEIGHT);
            jb3.setBounds(260, 10, CARD_WIDTH, CARD_HEIGHT);
            jb4.setBounds(0,90, CARD_HEIGHT, CARD_WIDTH);
            jb5.setBounds(905,90, CARD_HEIGHT, CARD_WIDTH);
            //this.add(jb1);
            //this.add(jb2);
            //this.add(jb3); 
            //this.add(jb4);
            //this.add(jb5);
            
        }catch (IOException ex) {
            ex.printStackTrace();
        } 
*/

 /* line 624: Change due to overlapping of images
        for(int i = 0; i < temp.size(); i++){
            if(count % 8 == 0){
                y += 110;
                x = 120;
                z = 0;
            }
            if(cardsBePlaced.size() > 30){
                System.out.println("Cards combo over 30");
                return;
            }

            ArrayList<Card> currentCards = temp.get(i);
            for(int j = 0; j < currentCards.size(); j++){

                Card cCard = currentCards.get(j);
                String fileName = cCard.getRank().toString() + cCard.getSuit().toString() + ".png";
                String cardName = cCard.getRank().toString() + cCard.getSuit().toString();
                JLabel currLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
                currLabel.setBounds(x+z, y, CARD_WIDTH, CARD_HEIGHT);
                currLabel.setName(cardName);
                this.add(currLabel);
                currJLs.add(currLabel);
                System.out.println("X: " + x + ", y: " + y + ", z: " + z + ", Count: " + count);
                z += 10;

            }
            count ++;
            x += 60;
        }
        */
         /* Line 303 *2* - Rescale image display
               for(ArrayList<JLabel> arrJL: allTableCards){
                    
                    for(int i = 0; i < arrJL.size(); i++){
                        JLabel currentLabel = arrJL.get(i);
                        Rectangle bound = currentLabel.getBounds();
                        if(bound.contains(e.getPoint())){

                            if(!(selectedTable.contains(currentLabel))){
                                selectedTable.add(currentLabel);
                                
                                currentLabel.setBounds(currentLabel.getX(), currentLabel.getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                //resetBoard();
                                //currentLabel.setVisible(false);
                                repaint();
                                return;
                            }else{
                                selectedTable.remove(currentLabel);
                                //setBoardInvisible();
                                currentLabel.setBounds(currentLabel.getX(), currentLabel.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                //resetBoard();
                                repaint();
                                return;
                            }
                        }

                    }
               }


                
                System.out.println("Bool: " + tableCombo + " | table: " + selectedTable.size());
                //printallTable();

               
                for(ArrayList<JLabel> arrJL : allTableCards){
                    int max = arrJL.size() - 1;

                    for(int i = 0; i < arrJL.size(); i++){
                        JLabel currentLabel = arrJL.get(i);
                        Rectangle bd2 = currentLabel.getBounds();
                        if(bd2.contains(e.getPoint())){
                            if(!(selectedTable.contains(currentLabel)) && i == 0){
                                selectedTable.add(currentLabel);
                                selectedCombo.add(arrJL);
    
                                currentLabel.setBounds(currentLabel.getX(), currentLabel.getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                repaint();
                                resetFrame();
                                //return;
                            }else if(!(selectedTable.contains(currentLabel)) && i == max){
                                selectedTable.add(currentLabel);
                                selectedCombo.add(arrJL);
    
                                currentLabel.setBounds(currentLabel.getX(), currentLabel.getY() - 10, CARD_WIDTH, CARD_HEIGHT);
                                repaint();
                                resetFrame();
                                //return;
                            }else if(selectedTable.contains(currentLabel) && i == 0){
                                selectedTable.remove(currentLabel);
                                selectedCombo.remove(arrJL);
    
                                currentLabel.setBounds(currentLabel.getX(), currentLabel.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                repaint();
                                resetFrame();
                                //return;
                            }else if(selectedTable.contains(currentLabel) && i == max){
                                selectedTable.remove(currentLabel);
                                selectedCombo.remove(arrJL);
    
                                currentLabel.setBounds(currentLabel.getX(), currentLabel.getY() + 10, CARD_WIDTH, CARD_HEIGHT);
                                repaint();
                                resetFrame();
                                //return;
                            }
                            return;
                        }
                    }
                }
                */

            //Line 775, checking straight on dealing
             /*
    //One single card, and a single array containing the selected 
    public boolean dealSingleCards(Card singleCard, ArrayList<ArrayList<JLabel>> selectedCombo2, ArrayList<JLabel> selectedTable2){
        //check each combos on the table
        //check if the current card fits into one of them, if not return false
        boolean checkNormalStraight, checkSuitStraight;
        boolean findAce = false;
        ArrayList<JLabel> currCombo = selectedCombo2.get(0);

        ArrayList<Card> temp = new ArrayList<Card>();
        for(JLabel jl : currCombo){
            String name = jl.getName();
            if(name.contains("Ace")){
                findAce = true;
            }
            Card card = string2Card(name);
            temp.add(card);
        }
        temp.add(singleCard);

        System.out.println("Before: " + temp);
        boolean left_right;
        if(selectedCombo2.get(0).get(0).equals(selectedTable2.get(0))){
            System.out.println("    Left");
            left_right = false;
            int i = checkStraight2(temp, findAce, left_right);

        }else if(selectedCombo2.get(0).get(selectedCombo2.get(0).size()-1) == (selectedTable2.get(0))){
            System.out.println("    Right");
            left_right = true;
            int i = checkStraight2(temp, findAce, left_right);
        }
        System.out.println("Ace found? : " + findAce);



        //new method to check same suit straight (reference check checkStraight method)
        //checkNormalStraight = checkStraight(currentCards);

        //checkSuitStraight = findSuitStraight(currentCards);
        //System.out.println("Temp: " + currentCards + ", BooL: "+ checkNormalStraight + " , Bool 2: " + checkSuitStraight);
        return false;
    }

    // *Check straight for single dealing
    public int checkStraight2(ArrayList<Card> list, Boolean isACE, Boolean leftRight){
        int size = list.size()-1;
        
        Card joining = list.get(size-1);
        Card first =list.get(0);
        Card second = list.get(1);

        Card last = list.get(size-2);
        Card secondLast = list.get(size-3);
        //On the left
        if(leftRight == false){
            ArrayList<Card> temp = new ArrayList<>();
            temp.add(first);
            temp.add(second);
            temp.add(joining);

            boolean sameSuitsequence = checkStraight(temp);
            boolean suitStraight = findSuitStraight(temp);

            System.out.println("Sequence: " + sameSuitsequence + ", suit straight: " + suitStraight);
            
        }//Else it would be right
        else{
            ArrayList<Card> temp = new ArrayList<Card>();
            temp.add(secondLast);
            temp.add(last);
            temp.add(joining);

            boolean sameSuitsequence = checkStraight(temp);
            boolean suitStraight = findSuitStraight(temp);

            System.out.println("2. Sequence: " + sameSuitsequence + ", suit straight: " + suitStraight);
        }

       
        if(leftRight == false){
            if(isACE == true){
                if(!(first.getRank().equals(Rank.ACE))&& second.getRank().equals(Rank.TWO) && last.getRank().equals(Rank.KING)){
                    //If front card is not Ace, and second card is two, ensure no ace at last card
                    Card tempCard = joining;
                    list.remove(joining);
                    list.add(0, tempCard);

                    return 2;
                }else{
                    //Have ace already
                    return 0;
                }
            }
            //When t
            //Eample 3 ->+ 4,5,6
            list.add(0, joining);
            list.remove(size-1);

            boolean state = true;

            for(int i = 0; i< list.size()-1; i++){
                if(list.get(i).getRank().getRankValue() != list.get(i+1).getRank().getRankValue() -1){
                    state = false;
                }
            }

            System.out.println("List: " + list + ", State: " + state);

        }else{

        }
        

        return 0;
    }
    */

    //Initial Player Hand -> Original
    /*
         public void initialPlayerHand(Player player, int amount, Deck tabledeck){
        if(player.getPlayerName() == "Player 1"){
            Hand hand = player.getPlayerHand();
            int x = 240;
            for(int i = 0; i < amount; i++){
            hand.takeCardfromDeck(tabledeck);
            Card currentCard = hand.getCard(i);
            String fileName = currentCard.getRank().toString() + currentCard.getSuit().toString() + ".png";
            String cardName = currentCard.getRank().toString() + currentCard.getSuit().toString();

            JLabel currJLabel = new JLabel( new ImageIcon( new ImageIcon(IMAGE_DIR + fileName).getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
            currJLabel.setBounds(x, 680, CARD_WIDTH, CARD_HEIGHT);
            currJLabel.setName(cardName);
            this.add(currJLabel);

            playerCards.add(currJLabel);
            x += 30;
            }  
        }
    }
    
    */