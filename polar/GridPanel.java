package polar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GridPanel extends JLayeredPane{
	
	private JLabel coord_label;
	
	private int center_x, center_y, slice, width, height, radius;

	// X values for line segments
	private int r1x, r2x, r3x, r4x;

	// Y values for line segments
	private int r1y, r2y, r3y, r4y;

	// Coordinates for crosspoints
	private int px, py;

	private Dimension size;
	private ArrayList<Point> points = new ArrayList<Point>();
	
	//Radian values for diagonals
	private final double RADIAN1 = Math.PI / 6.0;
	private final double RADIAN1_X = Math.cos(RADIAN1);
	private final double RADIAN1_Y = Math.sin(RADIAN1);
	private final double RADIAN2 = Math.PI / 3.0;
	private final double RADIAN2_X = Math.cos(RADIAN2);
	private final double RADIAN2_Y = Math.sin(RADIAN2);
	private final double RADIAN3 = 2.0 * Math.PI / 3.0;
	private final double RADIAN3_X = Math.cos(RADIAN3);
	private final double RADIAN3_Y = Math.sin(RADIAN3);
	private final double RADIAN4 = 5.0 * Math.PI / 6.0;
	private final double RADIAN4_X = Math.cos(RADIAN4);
	private final double RADIAN4_Y = Math.sin(RADIAN4);
	private final Point2D.Double[] RADIANS = {
			new Point2D.Double(RADIAN1_X, RADIAN1_Y),
			new Point2D.Double(RADIAN2_X, RADIAN2_Y),
			new Point2D.Double(RADIAN3_X, RADIAN3_Y),
			new Point2D.Double(RADIAN4_X, RADIAN4_Y)
	};
	
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
				NodeLabel x = new NodeLabel("X", nearestPoint(mouse_x, mouse_y));
				grid.add(x, 1);
			}
		});
	}
	
	public Point nearestPoint(int x, int y) {
		Point nearest = new Point();
		double min_distance = Double.MAX_VALUE;
		double current_distance;
		for (Point p : points) {
			current_distance = p.distance(x,y);
			if ( current_distance < min_distance) {
				min_distance = current_distance;
				nearest = p;
			}
		}
		return nearest;
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
		
		//Draw circles and note crosspoints
		points.clear();
		for (int i = 1; i <= 4; i++) {
			radius += slice;
			for (Point2D.Double radian : RADIANS) {

				px = (int) Math.ceil( center_x + ( radius * radian.x) );
				py = (int) Math.ceil( center_y + ( radius * radian.y) );

				//Each radian has two associated crosspoints
				points.add(new Point(px, py));
				points.add(new Point(center_x-(px-center_x), center_y-(py-center_y)));
			}
			
			//Vertical/horizontal crosspoints
			points.add(new Point(center_x-radius, center_y));
			points.add(new Point(center_x+radius, center_y));
			points.add(new Point(center_x, center_y-radius));
			points.add(new Point(center_x, center_y+radius));
			
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
		r1x = (int) Math.ceil( center_x + ( radius * RADIAN1_X) );
		r1y = (int) Math.ceil( center_y + ( radius * RADIAN1_Y) );
		r2x = (int) Math.ceil( center_x + ( radius * RADIAN2_X) );
		r2y = (int) Math.ceil( center_y + ( radius * RADIAN2_Y) );
		r3x = (int) Math.ceil( center_x + ( radius * RADIAN3_X) );
		r3y = (int) Math.ceil( center_y + ( radius * RADIAN3_Y) );
		r4x = (int) Math.ceil( center_x + ( radius * RADIAN4_X) );
		r4y = (int) Math.ceil( center_y + ( radius * RADIAN4_Y) );
		
		//Draw intersecting diagonals
		g2.draw(new Line2D.Double(r1x,r1y,center_x-(r1x-center_x),center_y-(r1y-center_y)));
		g2.draw(new Line2D.Double(r2x,r2y,center_x-(r2x-center_x),center_y-(r2y-center_y)));
		g2.draw(new Line2D.Double(r3x,r3y,center_x-(r3x-center_x),center_y-(r3y-center_y)));
		g2.draw(new Line2D.Double(r4x,r4y,center_x-(r4x-center_x),center_y-(r4y-center_y)));
		
	}

}