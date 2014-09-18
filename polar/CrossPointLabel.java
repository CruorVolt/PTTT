package polar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;

public class CrossPointLabel extends JLabel{
	
	private boolean marked;
	private String player;
	private Point location;
	private double radian;

	public CrossPointLabel (String player, Point p, double radian) {
		
		marked = false;
		player = null;
		location = p;
		
		setText(player);
		setVerticalAlignment(JLabel.TOP);
		setHorizontalAlignment(JLabel.CENTER);
		setForeground(Color.RED);
		setFont(new Font("Arial", Font.BOLD, 20));
		setBounds(p.x-13, p.y-11, 26, 22);
	}

	public void setMarked(boolean m) {
		marked = m;
	}
	
	public boolean isMarked() {
		return marked;
	}
	
	public void resize(Dimension windowSize) {

	}
}
