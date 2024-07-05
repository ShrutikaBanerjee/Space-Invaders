import javax.swing.*;
import java.net.URL;
import java.awt.Color;
import java.awt.Dimension;

public class Home extends JFrame implements Runnable {
    Thread thread;

    Home() {
        setSize(1100, 600);
        setLocation(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel with preferred size 512x512
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(512, 512));
        panel.setBackground(Color.red);
        
        // Attempt to load the image from the classpath
        URL imgURL = getClass().getResource("space.png");
        if (imgURL != null) {
            ImageIcon i1 = new ImageIcon(imgURL);
            JLabel image = new JLabel(i1);
            image.setPreferredSize(new Dimension(400, 512));
            panel.add(image);
        } else {
            System.err.println("Couldn't find file: space.png");
        }

        add(panel);
        pack(); // Adjust the frame size to fit the preferred size of the panel
        setVisible(true);

        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            Thread.sleep(5000);
            new App();
            setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Home frame = new Home();
        int x = 1;
        for (int i = 1; i <= 400; x += 6, i += 6) {
            frame.setLocation(650 - x, 400 - i / 2);
            frame.setSize(i + 1 + x, i);
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
