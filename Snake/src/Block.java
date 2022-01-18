import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Block {
	private Image img; 	
	private AffineTransform tx;
	private double scale = 0.1; // scales for images
	
	private int x, y; // coordinates
	
	public Block(int x, int y) {
		this.x = x;
		this.y = y;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
	}
	
	public void changePicture(String path) { // change to different color live among us
		img = getImage(path);
		init(0, 0);
	}
	public void paint(Graphics g) {
		// these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		update();
		g2.drawImage(img, tx, null);
	}
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	private void update() {
		tx.setToTranslation(x, y);
		tx.scale(scale, scale);
	}
	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Block.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
