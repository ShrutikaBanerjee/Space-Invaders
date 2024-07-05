import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int score;
    boolean gameOver = false;

    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    // Ship 
    Block ship;
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocityX = 5; // Velocity of the ship

    // Aliens
    ArrayList<Block> aliensArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienCols = 3;

    int alienCount = 0; // Number of aliens to defeat
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocity = -10;

    Timer gameLoop;

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load Images
        shipImg = new ImageIcon(getClass().getResource("ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);

        aliensArray = new ArrayList<>();

        bulletArray = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);
        createAliens();
        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameOver) {
            drawGameOver(g);
        } else {
            draw(g);
        }
    }

    private void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y - 20, ship.width, ship.height, null);

        // Draw Aliens
        for (Block alien : aliensArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        // Draw Bullets
        g.setColor(Color.WHITE);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        // Draw Score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Game Over", boardWidth / 2 - 80, boardHeight / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press Enter to Restart", boardWidth / 2 - 110, boardHeight / 2 + 20);
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    public void move() {
        // Move Aliens
        for (Block alien : aliensArray) {
            if (alien.alive) {
                alien.x += alienVelocityX;
                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;
                    // Move aliens down
                    for (Block a : aliensArray) {
                        a.y += alienHeight;
                        // Check if aliens reach the ship
                        if (a.y + a.height >= ship.y) {
                            gameOver = true;
                        }
                    }
                }
            }
        }

        // Move Bullets
        for (Block bullet : bulletArray) {
            bullet.y += bulletVelocity;

            // Bullet collision with aliens
            for (Block alien : aliensArray) {
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score++;
                }
            }
        }

        // Remove used bullets
        bulletArray.removeIf(bullet -> bullet.used || bullet.y < 0);

        // Check for new wave of aliens
        if (alienCount == 0 && !gameOver) {
            alienCols = Math.min(alienCols + 1, columns / 2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);
            aliensArray.clear();
            bulletArray.clear();
            createAliens();
        }
    }

    private void createAliens() {
        Random random = new Random();
        for (int r = 0; r < alienRows; r++) {
            for (int c = 0; c < alienCols; c++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                int xPos = alienX + c * alienWidth * 2; // Adjust spacing between columns
                int yPos = alienY + r * alienHeight * 2; // Adjust spacing between rows
                Block alien = new Block(xPos, yPos, alienWidth, alienHeight, alienImgArray.get(randomImgIndex));
                aliensArray.add(alien);
            }
        }
        alienCount = aliensArray.size();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                ship.x -= shipVelocityX;
                // Prevent the ship from moving off the screen
                if (ship.x < 0) {
                    ship.x = 0;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                ship.x += shipVelocityX;
                // Prevent the ship from moving off the screen
                if (ship.x > boardWidth - ship.width) {
                    ship.x = boardWidth - ship.width;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(ship.x + shipWidth / 2 - bulletWidth / 2, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            restartGame();
        }
    }

    private void restartGame() {
        score = 0;
        gameOver = false;
        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        aliensArray.clear();
        bulletArray.clear();
        alienCols = 3;
        alienRows = 2;
        createAliens();
    }
}
