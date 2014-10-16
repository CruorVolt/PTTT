package polar.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;

import polar.game.*;

public class PlayerPanel extends JPanel{
	
	protected Game game;
	protected Character token;
	private JLabel playerLabel;
	private JTextArea movesTextArea;
	private JScrollPane movesPane;
	
	public PlayerPanel(Game game, Character token) {
		super();
		this.game = game;
		this.token = token;
		setBackgroundColor();

		GridLayout gridLayout = new GridLayout(2,0);
		setLayout(gridLayout);

		playerLabel = new JLabel("Player " + this.token, JLabel.CENTER);
		add(playerLabel);
		

		movesTextArea = new JTextArea("");
		movesTextArea.setEditable(false);
		movesTextArea.setBackground(Color.CYAN);
		movesTextArea.setColumns(10);
		movesTextArea.setWrapStyleWord(true);

		movesPane = new JScrollPane(movesTextArea);
		add(movesPane);
	}
	
	public void update(PolarCoordinate coord, boolean turn) {
		if (!setBackgroundColor()) { // update belongs to this panel
			movesTextArea.setText(movesTextArea.getText() + coord.toString() + "\n");
		}
	}
	
	public boolean setBackgroundColor() {
		Player player = game.currentPlayer();
		if (player.getToken() == token) { //It is this panel's turn
			setBackground(Color.GREEN);
			return true;
		} else {
			setBackground(Color.RED);
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

}
