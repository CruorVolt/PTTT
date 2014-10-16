package polar.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import polar.game.*;

public class PlayerPanel extends JPanel{
	
	protected Game game;
	protected Character token;
	private JLabel playerLabel, turnLabel;
	
	public PlayerPanel(Game game, Character token) {
		super();
		this.game = game;
		this.token = token;
		playerLabel = new JLabel("Player " + this.token);
		turnLabel = new JLabel("");
		add(playerLabel);
		add(turnLabel);
		setBackgroundColor();
	}
	
	public void update() {
		setBackgroundColor();
	}
	
	public void setBackgroundColor() {
		Player player = game.currentPlayer();
		if (player.getToken() == token) { //It is this panel's turn
			setBackground(Color.GREEN);
		} else {
			setBackground(Color.RED);
		}
	}

	private static final long serialVersionUID = 1L;

}
