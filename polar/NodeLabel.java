package polar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;

public class NodeLabel extends JLabel {

	public NodeLabel (String player, Point p) {
		setText(player);
		setVerticalAlignment(JLabel.TOP);
		setHorizontalAlignment(JLabel.CENTER);
		setForeground(Color.RED);
		setFont(new Font("Arial", Font.BOLD, 20));
		setBounds(p.x-13, p.y-11, 26, 22);
	}

}
