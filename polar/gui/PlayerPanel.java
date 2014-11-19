package polar.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;

import logic.Heuristic;
import polar.game.*;

public class PlayerPanel extends JPanel{
	
	protected Game game;
	protected Character token;
	protected JLabel playerLabel, scoreLabel;
	protected JTextArea movesTextArea;
	protected JScrollPane movesPane;
	protected boolean player;
	
	public PlayerPanel(Game game, boolean player) {
		super();
		this.game = game;
		if(player==Player.PLAYER_X)
			this.token = 'X';
		else
			this.token = 'O';
		setBackgroundColor(false);

		GridLayout gridLayout = new GridLayout(0,1);
		setLayout(gridLayout);

		playerLabel = new JLabel("Player " + this.token, JLabel.CENTER);
		add(playerLabel);
		
		scoreLabel = new JLabel("NO SCORE YET");
		add(scoreLabel);

		movesTextArea = new JTextArea("");
		movesTextArea.setEditable(false);
		movesTextArea.setColumns(10);
		movesTextArea.setWrapStyleWord(true);

		movesPane = new JScrollPane(movesTextArea);
		movesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		movesPane.setBackground(new Color(0,0,0,0));
		add(movesPane);
	}
	
	public void update(PolarCoordinate coord, boolean turn) {
		if (!setBackgroundColor(turn)) { // update belongs to this panel
			movesTextArea.setText(movesTextArea.getText() + coord.toString() + "\n");
		}
		scoreLabel.setText("Score: " + Heuristic.evaluate(game.getMap(), player));
	}
	
	public boolean setBackgroundColor(boolean turn) {
		if ( (token=='X' && !turn) || (token=='O' && turn) ) { //It is this panel's turn
			setBackground(Color.GREEN);
			return true;
		} else {
			setBackground(Color.RED);
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

}
