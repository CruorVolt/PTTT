package polar.gui;

import java.awt.Graphics;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import polar.game.Game;

public class HumanPlayerPanel extends PlayerPanel{

	private static final long serialVersionUID = 1L;
	
	JLabel random;
	
	public HumanPlayerPanel(Game game, Character token){
		super(game, token);
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		JLabel label = new JLabel("I'M A HUMAN!");
		add(label);
	}
	
}
