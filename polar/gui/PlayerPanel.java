package polar.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import logic.Heuristic;
import polar.game.*;

public class PlayerPanel extends JPanel{
	
	protected Game game;
	protected Character token;
	protected JLabel playerLabel, scoreLabel, movesLabel;
	protected boolean player;
	protected int moves;
	
	public PlayerPanel(Game game, boolean player) {
		super();
		moves = 0;
		this.player = player;
		this.game = game;
		if(player==Player.PLAYER_X)
			this.token = 'X';
		else
			this.token = 'O';

		GridLayout gridLayout = new GridLayout(0,1);
		setLayout(gridLayout);

		playerLabel = new JLabel("Player " + this.token, JLabel.CENTER);
		playerLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		playerLabel.setOpaque(true);
		add(playerLabel);
		
		scoreLabel = new JLabel("No score yet", JLabel.CENTER);
		add(scoreLabel);

		movesLabel = new JLabel(moves + " moves", JLabel.CENTER);
		add(movesLabel);

		setBackgroundColor(false);
	}
	
	public void update(MoveReport report) {
		boolean turn = report.getMove().getPlayer();
		if (!setBackgroundColor(turn)) { // update belongs to this panel
			moves++;
			movesLabel.setText(moves + " moves");
		}
		scoreLabel.setText("Score: " + Heuristic.evaluate(game.getMap(), player));
	}
	
	public boolean setBackgroundColor(boolean turn) {
		if ( (token=='X' && !turn) || (token=='O' && turn) ) { //It is this panel's turn
			playerLabel.setBackground(Color.GREEN);
			return true;
		} else {
			playerLabel.setBackground(Color.RED);
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

}
