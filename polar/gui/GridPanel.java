package polar.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JLayeredPane;

import polar.game.Game;
import polar.game.Move;
import polar.game.MoveReport;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.exceptions.BadCoordinateException;

public class GridPanel extends JLayeredPane implements ComponentListener{
	
	private static final long serialVersionUID = 1L;

	private int center_x, center_y, slice, width, height, radius;

	// X values for line segments
	private int r1x, r2x, r3x, r4x;

	// Y values for line segments
	private int r1y, r2y, r3y, r4y;

	private Dimension size;
	private CrossPointLabel[][] points = new CrossPointLabel[4][12];
	private Integer newestX, newestY;
	private Game game;
	
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
	
	public GridPanel(Game game) throws BadCoordinateException {
		// Which ring layer of the grid the point belongs to
		for (int x = 1; x <= 4; x++) {
			// Which radian of the grid the point belongs to
			for (int y = 0; y <= 11; y++) {
				points[x-1][y] = new CrossPointLabel(game, new PolarCoordinate(new UnTestedCoordinates(x,y)));
				add(points[x-1][y]);
			}
		}
		addComponentListener(this);
		this.game = game;
	}
	
	//Update the indicated label with the new move
	public void update(MoveReport report) {
		if (newestX != null) {
			points[newestX - 1][newestY].setForeground(Color.RED);
		}
		newestX = report.getMove().getLoc().getX();
		newestY = report.getMove().getLoc().getY();
		points[newestX - 1][newestY].setText(game.currentPlayer().getToken().toString());
		points[newestX - 1][newestY].setForeground(Color.BLUE);

		//points[coord.getX() - 1][coord.getY()].setText(game.currentPlayer().getToken().toString());
	}
	
	public void componentResized(ComponentEvent e) { 
		double radian = Math.PI / 6.0;
		double current_radian = 0;
		
		// The cross-points need to be repositioned whenever the panel changes size
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 12; j++) {
				points[i-1][j-1].setLocation(getSize(), current_radian, i, 0.1);
				current_radian += radian;
			}
			current_radian = 0;
		}
	}
	
	public void componentMoved(ComponentEvent e) { }

	public void componentShown(ComponentEvent e) { }

	public void componentHidden(ComponentEvent e) { }

	// Unused unless we go back to raw Swing coordinate for finding points
	public CrossPointLabel nearestPoint(int x, int y) {
		CrossPointLabel nearest = new CrossPointLabel(null, null);
		double min_distance = Double.MAX_VALUE;
		double current_distance;
		for (CrossPointLabel[] inner : points) {
			for (CrossPointLabel p : inner) {
				current_distance = p.distance(x,y);
				if ( current_distance < min_distance) {
					min_distance = current_distance;
					nearest = p;
				}
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
		//Each 'donut' is 1/10th of the screen's width
		slice = (int) Math.ceil(width/10);
		center_x = (int) Math.ceil(width / 2.0);
		center_y = (int) Math.ceil(height / 2.0);
		
		//Draw layered circles
		for (int i = 1; i <= 4; i++) {
			radius += slice;
			g2.draw(new Ellipse2D.Double( center_x-radius, center_y-radius, radius*2, radius*2));
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
		
		//Fill in center circle
		Ellipse2D.Double center = new Ellipse2D.Double(center_x - (slice-1) ,center_y - (slice-1), 2*(slice-1), 2*(slice-1)); 
		g2.draw(center);
		g2.setColor(getBackground());
		g2.fill(center);
		
	}
	
	public void updateWin(Move[] winState) {
		PolarCoordinate coord;
		for (Move move : winState) {
			coord = move.getLoc();
			CrossPointLabel point = points[coord.getX() - 1][coord.getY()];
			point.setForeground(new Color(0, 170, 0)); //Dark green winning nodes
		}
		
	}


}