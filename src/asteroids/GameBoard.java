package asteroids;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Holds the information needed for creation of the game board
 * and also is used as the main driver for the application.
 * @author Casey Scarborough
 * @since 2013-05-19
 * @version 1.0.2
 * @see Rock
 * @see SpaceShip
 * @see Key
 * @see GameDrawingPanel
 * @see RepaintTheBoard
 */
@SuppressWarnings("serial")
public class GameBoard extends JFrame {
	// Height and width of the gameboard
	public static int boardWidth = 1000;
	public static int boardHeight = 800;
	
	public static boolean keyHeld = false;
	public static int keyHeldCode;
	
	/**
	 * This is the main method and driver for the application.
	 * It sole purpose is to create the game board.
	 * @param args
	 */
	public static void main(String[] args) {
		new GameBoard();
	}

	/**
	 * The constructor for the GameBoard class; sets the size, 
	 * title, default close operation, and creates a new game panel.
	 */
	public GameBoard() {
		this.setSize(boardWidth, boardHeight);
		this.setTitle("Java Asteroids");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == Key.W) {
					System.out.println("Forward"); 
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if (e.getKeyCode() == Key.S) {
					System.out.println("Backward"); 
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if (e.getKeyCode() == Key.D) {
					System.out.println("Rotate Right"); 
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				 } else if (e.getKeyCode() == Key.A) {
					System.out.println("Rotate Left"); 
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				} else if( e.getKeyCode() == Key.E) {
					System.out.println("Stopping ship..."); 
					keyHeldCode = e.getKeyCode();
					keyHeld = true;
				}
			}
			
			// When the key is released, lets the object know by setting
			// keyHeld to false
			@Override
			public void keyReleased(KeyEvent e) {
				keyHeld = false;
			}
			
		});
		
		GameDrawingPanel gamePanel = new GameDrawingPanel();
		this.add(gamePanel, BorderLayout.CENTER);
		
		// Create a new thread pool with 5 threads
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		// Repaint the game board every 20ms, method, initial delay, subsequent delay, time unit
		executor.scheduleAtFixedRate(new RepaintTheBoard(this), 0L, 20L, TimeUnit.MILLISECONDS);
		this.setResizable(false);
		this.setVisible(true);
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
	 * An ArrayList used to hold all of the currently displayed rocks.
	 */
	public ArrayList<Rock> rocks = new ArrayList<>();
	
	// Get the x and y points for the rock
	int[] polyXArray = Rock.sPolyXArray;
	int[] polyYArray = Rock.sPolyYArray;
	
	SpaceShip spaceShip = new SpaceShip();
	
	// Get the game board's height and width
	int width = GameBoard.boardWidth;
	int height = GameBoard.boardHeight;
	
	// Create 15 Rock objects and store them in our rocks ArrayList
	public GameDrawingPanel() {
		for(int i = 0; i < 15; i++) {
			// Get a random x and y starting position, the -40 is to keep rock on the screen
			int randomStartXPos = (int) (Math.random() * (GameBoard.boardWidth - 40) + 1);
			int randomStartYPos = (int) (Math.random() * (GameBoard.boardHeight - 40) + 1);
			
			// Create the rock using the constructor and add it to our array
			rocks.add(new Rock(Rock.getPolyXArray(randomStartXPos), Rock.getPolyYArray(randomStartYPos), 13, randomStartXPos, randomStartYPos));
			Rock.rocks = rocks;
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
		
		// Cycle through all rocks in rocks ArrayList
		for(Rock rock : rocks) {
			rock.move(); // Move the rock
			graphicSettings.draw(rock); // Draw it on the screen
		}
		
		// Check to see if the D or A keys are being held and spins the ship in the correct direction
		if(GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.D) {
			spaceShip.increaseRotationAngle();
			System.out.println("Rotation angle: " + spaceShip.getRotationAngle()); 
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.A) {
			spaceShip.decreaseRotationAngle();
			System.out.println("Rotation angle: " + spaceShip.getRotationAngle()); 
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.W) {
			spaceShip.setMovingAngle(spaceShip.getRotationAngle());
			spaceShip.increaseXVelocity(spaceShip.shipXMoveAngle(spaceShip.getMovingAngle())*0.1);
			spaceShip.increaseYVelocity(spaceShip.shipYMoveAngle(spaceShip.getMovingAngle())*0.1);
			System.out.println("Speeding up at angle: " + spaceShip.getMovingAngle()); 
		} else if (GameBoard.keyHeld == true && GameBoard.keyHeldCode == Key.S) {
			spaceShip.setMovingAngle(spaceShip.getRotationAngle());
			spaceShip.decreaseXVelocity(spaceShip.shipXMoveAngle(spaceShip.getMovingAngle())*0.1);
			spaceShip.decreaseYVelocity(spaceShip.shipYMoveAngle(spaceShip.getMovingAngle())*0.1);
			System.out.println("Slowing down at angle: " + spaceShip.getMovingAngle()); 
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
	}
}