package asteroids;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * Holds the information needed for creation of the game board
 * and also is used as the main driver for the application.
 * @author Casey Scarborough
 * @since 2013-05-19
 * @version 1.1.1
 * @see Asteroid
 * @see SpaceShip
 * @see Key
 * @see GameDrawingPanel
 * @see RepaintTheBoard
 */
@SuppressWarnings("serial")
public class GameBoard extends JFrame {
	// Height and width of the gameboard
	public static int boardWidth = 900;
	public static int boardHeight = 720;
	public static int numberOfAsteroids = 15;
	
	public static boolean keyHeld = false;
	public static int keyHeldCode;
	public static ArrayList<Laser> lasers = new ArrayList<>();
	public static int points = 0;
	public static JLabel score;
	public static JPanel scorePanel, resultsPanel;
	public static JLabel results;
	public static JLayeredPane lpane;
	public static JButton restart, exit;
	
	public static boolean soundOn = true;
	
	static int shotsFired = 0;

	/**
	 * This is the main method and driver for the application.
	 * It sole purpose is to create the game board.
	 * @param args
	 */
	public static void main(String[] args) {
		new WelcomeScreen();
	}

	/**
	 * The constructor for the GameBoard class; sets the size, 
	 * title, default close operation, and creates a new game panel.
	 */
	public GameBoard(int numberOfAsteroids, boolean soundOn) {
		GameBoard.numberOfAsteroids = numberOfAsteroids;
		GameBoard.soundOn = soundOn;
		this.setSize(boardWidth, boardHeight);
		this.setTitle("Java Asteroids");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.centerWindow();
		
		score = new JLabel();
		score.setFont(new Font("Sans-serif", Font.PLAIN, 20));
		score.setForeground(Color.WHITE);
		score.setText("Score: " + points);
		
		results = new JLabel();
		results.setFont(new Font("Sans-serif", Font.PLAIN, 20));
		results.setForeground(Color.WHITE);
		
		scorePanel = new JPanel(new BorderLayout());
		scorePanel.setBackground(Color.BLACK);
		
		resultsPanel = new JPanel(new BorderLayout());
		resultsPanel.setBackground(Color.BLACK);
		
		restart = new JButton("Restart Game");
		exit = new JButton("Exit");
		
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == restart) {
					closeWindow();
					GameBoard.main(null);
				}
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == exit) { System.exit(0); }
			}
		});
		
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			// Check for key presses in the game
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == Key.W) {
					SpaceShip.interaction = true;
					if (GameBoard.soundOn == true) { Sound.playSoundEffect(Sound.thrust); }
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if (e.getKeyCode() == Key.S) {
					SpaceShip.interaction = true;
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if (e.getKeyCode() == Key.D) {
					SpaceShip.interaction = true;
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				 } else if (e.getKeyCode() == Key.A) {
					SpaceShip.interaction = true;
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if( e.getKeyCode() == Key.E) {
					System.out.println("Stopping ship..."); 
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if (e.getKeyCode() == Key.ENTER) {
					SpaceShip.interaction = true;
					if (GameBoard.soundOn == true) { Sound.playSoundEffect(Sound.laser); }
					lasers.add(new Laser(GameDrawingPanel.spaceShip.getShipNoseX(), 
							GameDrawingPanel.spaceShip.getShipNoseY(),
							GameDrawingPanel.spaceShip.getRotationAngle()));
					shotsFired += 1;
				}
			}
			
			// When the key is released, lets the object know by setting
			// keyHeld to false
			@Override
			public void keyReleased(KeyEvent e) {
				keyHeld = false;
			}
			
		});
		
		// Create new game panel
		GameDrawingPanel gamePanel = new GameDrawingPanel();
		this.add(gamePanel, BorderLayout.CENTER);
		
		// Create panel for score and results and add it to the screen
		scorePanel.add(score, BorderLayout.WEST);
		this.add(scorePanel, BorderLayout.NORTH);
		this.add(resultsPanel, BorderLayout.SOUTH);
		
		// Create a new thread pool with 5 threads
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
		// Repaint the game board every 20ms, method, initial delay, subsequent delay, time unit
		executor.scheduleAtFixedRate(new RepaintTheBoard(this), 0L, 20L, TimeUnit.MILLISECONDS);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	
	public static void displayResults(int asteroidsDestroyed, int timesExploded) {
		DecimalFormat twoDecimalPlaces = new DecimalFormat("#.##");
		double accuracy = ((double)asteroidsDestroyed/(double)GameBoard.shotsFired)*100;
		accuracy = Double.valueOf(twoDecimalPlaces.format(accuracy));
		results.setText("Times Exploded: " + timesExploded + "  Asteroids Destroyed: " + asteroidsDestroyed + "  Shots Fired: " + GameBoard.shotsFired + "  Accuracy: " + accuracy + "%");
		resultsPanel.add(results, BorderLayout.WEST);
		resultsPanel.add(exit, BorderLayout.EAST);
		scorePanel.add(GameBoard.restart, BorderLayout.EAST);
	}
	
	private void closeWindow() { 
		this.dispose();
		GameBoard.points = 0;
		GameBoard.shotsFired = 0;
		
		Asteroid.resetAsteroidsDestroyed();
		Asteroid.resetTimesExploded();
	}
	
	private void centerWindow() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = this.getSize().width;
		int height = this.getSize().height;
		int x = (dim.width - width) / 2;
		int y = (dim.height - height) / 2;
		this.setLocation(x, y);
	}
}



/**
 * Implements Runnable interface. It is a thread that will continually redraw
 * the screen while all other code still executes
 * @author Casey Scarborough
 *
 */
class RepaintTheBoard implements Runnable {
	GameBoard gameBoard;
	public RepaintTheBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		gameBoard.repaint();
	}
}


/**
 * The GameDrawingPanel class extends JComponent and contains the objects
 * that will be used in the game. It is essentially the game panel.
 * @author Casey Scarborough
 */
@SuppressWarnings("serial")
class GameDrawingPanel extends JComponent {
	
	/**
	 * An ArrayList used to hold all of the currently displayed asteroids.
	 */
	public ArrayList<Asteroid> asteroids = new ArrayList<>();
	
	// Get the x and y points for the asteroid
	int[] polyXArray = Asteroid.getStartingPolyXArray();
	int[] polyYArray = Asteroid.getStartingPolyYArray();
	
	static SpaceShip spaceShip = new SpaceShip();
	
	// Get the game board's height and width
	int width = GameBoard.boardWidth;
	int height = GameBoard.boardHeight;
	
	// Create 15 asteroid objects and store them in our asteroids ArrayList
	public GameDrawingPanel() {
		for(int i = 0; i < GameBoard.numberOfAsteroids; i++) {
			// Get a random x and y starting position, the -40 is to keep asteroid on the screen
			int randomStartXPos = (int) (Math.random() * (GameBoard.boardWidth - 40) + 1);
			int randomStartYPos = (int) (Math.random() * (GameBoard.boardHeight - 40) + 1);
			
			// Create the asteroid using the constructor and add it to our array
			asteroids.add(new Asteroid(Asteroid.getPolyXArray(randomStartXPos), Asteroid.getPolyYArray(randomStartYPos), 13, randomStartXPos, randomStartYPos));
			Asteroid.asteroids = asteroids;
		}
	}
	
	/**
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		Graphics2D graphicSettings = (Graphics2D) g;
		
		AffineTransform identity = new AffineTransform();
		
		// Fill the background width black the height and width of the game board
		graphicSettings.setColor(Color.BLACK);
		graphicSettings.fillRect(0, 0, getWidth(), getHeight());
		
		// Set the rendering rules
		graphicSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphicSettings.setPaint(Color.WHITE);
		
		// Cycle through all asteroids in asteroids ArrayList
		for(Asteroid asteroid : asteroids) {
			if(asteroid.onScreen) {
				asteroid.move(spaceShip, GameBoard.lasers); // Move the asteroid
				graphicSettings.draw(asteroid); // Draw it on the screen
			}
		}
		
		// Check to see if the D or A keys are being held and spins the ship in the correct direction
		if(GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.D) {
			spaceShip.increaseRotationAngle();
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.A) {
			spaceShip.decreaseRotationAngle();
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.W) {
			spaceShip.setMovingAngle(spaceShip.getRotationAngle());
			spaceShip.increaseXVelocity(spaceShip.shipXMoveAngle(spaceShip.getMovingAngle())*0.1);
			spaceShip.increaseYVelocity(spaceShip.shipYMoveAngle(spaceShip.getMovingAngle())*0.1);
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.S) {
			spaceShip.setMovingAngle(spaceShip.getRotationAngle());
			spaceShip.decreaseXVelocity(spaceShip.shipXMoveAngle(spaceShip.getMovingAngle())*0.1);
			spaceShip.decreaseYVelocity(spaceShip.shipYMoveAngle(spaceShip.getMovingAngle())*0.1);
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.E) {
			spaceShip.stopShip();
		}
		
		spaceShip.move();
		
		// Sets the origin of the screen
		graphicSettings.setTransform(identity);
		// Moves the ship to the center of the screen
		graphicSettings.translate(spaceShip.getXCenter(), spaceShip.getYCenter());
		// Rotate the ship
		graphicSettings.rotate(Math.toRadians(spaceShip.getRotationAngle()));
		
		graphicSettings.draw(spaceShip);
		
		// Draw lasers
		for (Laser laser : GameBoard.lasers) {
			laser.move();
			if(laser.onScreen) {
				graphicSettings.setTransform(identity);
				graphicSettings.translate(laser.getXCenter(), laser.getYCenter());
				graphicSettings.draw(laser);
			}
		}
	}
}
