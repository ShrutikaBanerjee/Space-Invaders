import javax.swing.*;
import java.awt.*;
public class App {
	App(){

        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns;
        int boardHeight = tileSize * rows;
        
        JFrame frame = new JFrame("SPACE INVADERS");
        frame.setLocation(400, 100);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        
        
        SpaceInvaders spaceInvaders = new SpaceInvaders();
        frame.add(spaceInvaders);
        spaceInvaders.requestFocus();
        
        frame.setVisible(true);
	}
    public static void main(String[] args) throws Exception {
    	new App();
    }
}

