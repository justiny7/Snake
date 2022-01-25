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
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {
	// game properties
	private int width = 607, height = 660, blockSize = 40;
	private int rows = 15, cols = 15;
	private int score, highScore = 0;
	private int numBarriers = 10;
	private int viewRadius = 5;
	private int direction; // 0 = right, 1 = down, 2 = left, 3 = up, 4 = none
	private int[] dX = {1, 0, -1, 0, 0}, dY = {0, 1, 0, -1, 0};
	boolean gameOver = true, buffed = false, eatBuffed = false;
	ArrayList<Block> snake, barriers, background;
	Deque<Integer> ops = new LinkedList<Integer>();
	Block apple;
	
	private void makeBackground() {
		background = new ArrayList<Block>();
		for (int i = 0; i < cols; ++i)
			for (int j = 0; j < rows; ++j)
				background.add(new Block(i, j, 3));
	}
	private int rand(int lo, int hi) { // random number between lo and hi
		return (int)(Math.random() * (hi - lo + 1)) + lo;
	}
	private void moveApple() {
		ArrayList<Block> pos = new ArrayList<Block>();
		for (int i = 0; i < cols; ++i) {
			for (int j = 0; j < rows; ++j) {
				boolean ok = true;
				for (Block b : snake)
					ok &= (b.getX() != i || b.getY() != j);
				
				if (ok)
					pos.add(new Block(i, j, 0));
			}
		}
		
		apple = pos.get(rand(0, pos.size() - 1));
		
		int rng = rand(1, 100);
		if (rng % 10 == 0) {
			apple.changePicture("/imgs/buffed_apple.png");
			buffed = true;
		} else {
			buffed = false;
		}
	}
	private int sq(int x) {
		return x * x;
	}
	int distance(Block a, Block b) {
		return (int)Math.floor(Math.sqrt(sq(a.getX() - b.getX()) + sq(a.getY() - b.getY())));
	}
	private void genBarriers() {
		ArrayList<Block> pos = new ArrayList<Block>();
		for (int i = 0; i < cols; ++i) {
			for (int j = 0; j < rows; ++j) {
				Block here = new Block(i, j, 2);
				if (distance(snake.get(snake.size() - 1), here) <= 4)
					continue;
				
				boolean ok = (i != apple.getX() || j != apple.getY());
				for (Block b : snake)
					ok &= (b.getX() != i || b.getY() != j);
				
				if (ok)
					pos.add(here);
			}
		}
		
		barriers = new ArrayList<Block>();
		while (barriers.size() < numBarriers) {
			int ind = rand(0, pos.size() - 1);
			barriers.add(pos.remove(ind));
		}
	}
	private boolean canGetApple() {
		int bad = 0;
		for (int i = 0; i < 4; ++i) {
			int nX = apple.getX() + dX[i], nY = apple.getY() + dY[i];
			if (nX < 0 || nX >= cols || nY < 0 || nY >= rows)
				++bad;
			else {
				boolean f = false;
				for (Block b : barriers)
					f |= (b.getX() == nX && b.getY() == nY);
				
				if (f)
					++bad;
			}
		}
		
		return bad != 4;
	}
	private void resetBarriers() {
		do {
			genBarriers();
		} while (!canGetApple());
	}
	private String getHeadType() {
		if (direction == 4 || distance(apple, snake.get(snake.size() - 1)) <= 5)
			return "tongue";
		else
			return "head";
	}
	private void setHead(int dir) {
		Block b = snake.get(snake.size() - 2);
		b.changePicture("/imgs/snakebody_" + (dir % 2) + ".png");
		Block head = snake.get(snake.size() - 1);
		head.changePicture("/imgs/snake" + getHeadType() + "_" + dir + ".png");
	}
	private void resetSnake() {
		direction = 4;
		score = 0;
		
		snake = new ArrayList<Block>();
		snake.add(new Block(1, 6, 1));
		snake.add(new Block(2, 6, 1));
		snake.add(new Block(3, 6, 1));
		snake.add(new Block(4, 6, 1));
		setHead(0);
	}
	private void reset() { // reset round
		buffed = eatBuffed = false;
		ops.clear();
		makeBackground();
		resetSnake();
		moveApple();
		resetBarriers();
	}
	private void moveSnake() {
		if (direction == 4)
			return;
		
		Block head = snake.get(snake.size() - 1);
		int nX = head.getX() + dX[direction];
		int nY = head.getY() + dY[direction];
		
		boolean bad = (nX < 0 || nX >= cols) || (nY < 0 || nY >= rows);
		for (Block b : snake)
			bad |= (b.getX() == nX && b.getY() == nY);
		for (Block b : barriers)
			bad |= (b.getX() == nX && b.getY() == nY);
		
		if (bad)
			gameOver = true;
		else {
			snake.add(new Block(nX, nY, 1));
			setHead(direction);
			if (apple.getX() == nX && apple.getY() == nY) {
				eatBuffed = buffed;
				moveApple();
				resetBarriers();
				highScore = Math.max(highScore, ++score);
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
		for (Block b : background) {
			if (eatBuffed || distance(b, snake.get(snake.size() - 1)) <= viewRadius)
				b.changePicture("/imgs/floor.png");
			else
				b.changePicture("imgs/floor_dark.png");
			b.paint(g);
		}
		apple.paint(g);
		for (Block b : snake)
			b.paint(g);
		for (Block b : barriers)
			if (eatBuffed || distance(b, snake.get(snake.size() - 1)) <= viewRadius)
				b.paint(g);
		
		Font font = new Font("Consolas", Font.BOLD, 20);
		g.setFont(font);
		
		g.setColor(Color.BLACK);
		g.drawString("Score: " + score, 5, 625);
		g.drawString("High score: " + highScore, 425, 625);
		
		if (!ops.isEmpty())
			direction = ops.removeFirst();
		
		moveSnake();
	}
	
	public static void main(String[] arg) {
		Music bg = new Music("mii.wav", true);
		bg.play();
		
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
		
		Timer t = new Timer(120, this);
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
		int test = direction;
		if (!ops.isEmpty())
			test = ops.peekLast();
		
		if (test != dir && test % 4 != (dir + 2) % 4)
			ops.addLast(dir);
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
