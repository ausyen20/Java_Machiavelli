import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        //create game window
        JFrame frame = new JFrame("Machiavelli");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        Game machiavelli = new Game(frame);
        frame.add(machiavelli);  
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
