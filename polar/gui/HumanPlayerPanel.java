package polar.gui;

import javax.swing.border.BevelBorder;

import polar.game.Game;

public class HumanPlayerPanel extends PlayerPanel{

	private static final long serialVersionUID = 1L;
	
	
	public HumanPlayerPanel(Game game, Character token){
		super(game, token);
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		playerLabel.setText("Human " + playerLabel.getText());
	}
	
}
