package polar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GridPanel extends JLayeredPane{
	
	private JLabel coord_label;
	
	private int center_x, center_y, slice, width, height, radius;
	private int r1x, r2x, r3x, r4x;
	private int r1y, r2y, r3y, r4y;
	private Dimension size;
	
	public GridPanel() {
		
		setLayout(null);
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		GridPanel grid = this;
		grid.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mouse_x = e.getX();
				int mouse_y = e.getY();
				System.out.println("(" + mouse_x + ", " + mouse_y + ")");
				JLabel x = createPoint("X", mouse_x, mouse_y);
				grid.add(x, 1);
			}
		});
	}
	
	private JLabel createPoint(String player, int x, int y) {
		JLabel label = new JLabel(player);
		label.setVerticalAlignment(JLabel.TOP);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setOpaque(false);
		label.setForeground(Color.RED);
		label.setFont(new Font("Arial", Font.BOLD, 20));
		label.setBounds(x-10, y-10, 20, 20);
		return label;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.GRAY);
		
		radius = 0;
		size = this.getSize();
		width = size.width;
		height = size.height;
		slice = (int) Math.ceil(width/10);
		center_x = (int) Math.ceil(width / 2.0);
		center_y = (int) Math.ceil(height / 2.0);
		
		//Draw circles
		for (int i = 1; i <= 4; i++) {
			radius += slice;
			g2.draw(new Ellipse2D.Double(
					center_x-radius, 
					center_y-radius, 
					radius*2, 
					radius*2
			));
		}

		// Draw vertical and horizontal lines
		g2.draw(new Line2D.Double(center_x-radius, center_y, center_x+radius, center_y));
		g2.draw(new Line2D.Double(center_x, center_y-radius, center_x, center_y+radius));
		
		//Coordinates for intersecting diagonals
		r1x = (int) Math.ceil( center_x + ( radius * Math.cos(Math.PI / 6.0)) );
		r1y = (int) Math.ceil( center_y + ( radius * Math.sin(Math.PI / 6.0)) );
		r2x = (int) Math.ceil( center_x + ( radius * Math.cos(Math.PI / 3.0)) );
		r2y = (int) Math.ceil( center_y + ( radius * Math.sin(Math.PI / 3.0)) );
		r3x = (int) Math.ceil( center_x + ( radius * Math.cos(2.0 * Math.PI / 3.0)) );
		r3y = (int) Math.ceil( center_y + ( radius * Math.sin(2.0 * Math.PI / 3.0)) );
		r4x = (int) Math.ceil( center_x + ( radius * Math.cos(5.0 * Math.PI / 6.0)) );
		r4y = (int) Math.ceil( center_y + ( radius * Math.sin(5.0 * Math.PI / 6.0)) );
		
		//Draw intersecting diagonals
		g2.draw(new Line2D.Double(r1x,r1y,center_x-(r1x-center_x),center_y-(r1y-center_y)));
		g2.draw(new Line2D.Double(r2x,r2y,center_x-(r2x-center_x),center_y-(r2y-center_y)));
		g2.draw(new Line2D.Double(r3x,r3y,center_x-(r3x-center_x),center_y-(r3y-center_y)));
		g2.draw(new Line2D.Double(r4x,r4y,center_x-(r4x-center_x),center_y-(r4y-center_y)));
		
	}

}
