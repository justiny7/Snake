import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {
	// game properties
	private int width = 607, height = 630, blockSize = 40;
	private int rows = 15, cols = 15;
	private int score;
	private int aX, aY, sY, sX;
	private int direction; // 0 = right, 1 = down, 2 = left, 3 = up, 4 = none
	private int[] dX = {1, 0, -1, 0, 0}, dY = {0, 1, 0, -1, 0};
	boolean gameOver = true, buffed = false;
	ArrayList<Block> snake;
	Block apple;
	
	// images + music
	
	// character properties
	
	private int rand(int lo, int hi) { // random number between lo and hi
		return (int)(Math.random() * (hi - lo + 1)) + lo;
	}
	private void moveApple() {
		aX = rand(0, cols - 1);
		aY = rand(0, rows - 1);
		apple = new Block(aX, aY, 0);
		
		int rng = rand(1, 100);
		if (rng % 5 == 0) {
			apple.changePicture("/imgs/buffed_apple.png");
			buffed = true;
		} else {
			buffed = false;
		}
	}
	private void reset() { // reset round
		moveApple();
		
		snake = new ArrayList<Block>();
		snake.add(new Block(1, 6, 1));
		snake.add(new Block(2, 6, 1));
		snake.add(new Block(3, 6, 1));
		snake.add(new Block(4, 6, 1));
		sX = 4;
		sY = 6;
		direction = 4;
		score = 0;
	}
	private void moveSnake() {
		if (direction == 4)
			return;
		
		int nX = sX + dX[direction];
		int nY = sY + dY[direction];
		
		boolean bad = (nX < 0 || nX >= cols) || (nY < 0 || nY >= rows);
		for (Block b : snake)
			bad |= (b.getX() == nX && b.getY() == nY);
		
		if (bad)
			gameOver = true;
		else {
			sX = nX;
			sY = nY;
			snake.add(new Block(sX, sY, 1));
			if (aX == sX && aY == sY) {
				moveApple();
				++score;
			} else {
				snake.remove(0);
			}
		}
	}
	public void paint(Graphics g) {
		if (gameOver) {
			reset();
			gameOver = false;
			return;
		}
		
		super.paintComponent(g);
		apple.paint(g);
		for (Block b : snake)
			b.paint(g);
		
		moveSnake();
	}
	
	public static void main(String[] arg) {
		Frame f = new Frame();
	}
	
	public Frame() {
		JFrame f = new JFrame("Snake!");
		f.setSize(new Dimension(width, height));
		f.add(this);
		f.setResizable(false);
		f.setLayout(new GridLayout(1,2));
		f.addMouseListener(this);
		f.addMouseMotionListener(this);
		f.addKeyListener(this);
		
		Timer t = new Timer(70, this);
		t.start();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) { // remove cursor
		// TODO Auto-generated method stub
		Toolkit tk = Toolkit.getDefaultToolkit();
		Point hs = new Point(0, 0);
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		Cursor c = tk.createCustomCursor(bi, hs, "Invisible");
		setCursor(c);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent arg0) { // mouse clicking logic
	}
	private boolean between(int lo, int hi, int x) { // helper function for checking if lo <= x <= hi
		return lo <= x && x <= hi;
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) { // tracking mouse movement for crosshairs
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	private void changeDir(int dir) {
		if (direction == dir || direction == (dir + 2) % 4)
			return;
		
		int nX = sX + dX[dir], nY = sY + dY[dir];
		if (nX != snake.get(score + 2).getX() || nY != snake.get(score + 2).getY())
			direction = dir;
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key = arg0.getKeyCode();
		
		if (key == KeyEvent.VK_UP)
			changeDir(3);
		else if (key == KeyEvent.VK_LEFT)
			changeDir(2);
		else if (key == KeyEvent.VK_DOWN)
			changeDir(1);
		else if (key == KeyEvent.VK_RIGHT)
			changeDir(0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

}
