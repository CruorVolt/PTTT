package polar.gui;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import polar.game.Game;

public class AIPlayerPanel extends PlayerPanel{

	private static final long serialVersionUID = 1L;
	
	JLabel random;
	
	public AIPlayerPanel(Game game, Character token){
		super(game, token);
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		playerLabel.setText("AI " + playerLabel.getText());
	}
	
}
