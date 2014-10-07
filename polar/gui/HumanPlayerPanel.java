package polar.gui;

import java.awt.Graphics;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import polar.game.Game;

public class HumanPlayerPanel extends PlayerPanel{

	private static final long serialVersionUID = 1L;
	
	JLabel random;
	
	public HumanPlayerPanel(Game game){
		JLabel label = new JLabel("I'M A HUMAN!");
		random = new JLabel("NUMBERS");
		add(label);
		add(random);
	}
	
	@Override
	public void paint(Graphics g) {
		Random num = new Random();
		int thing = num.nextInt();
		//random.setText("NUMBER: " + thing);

		
	}

}
