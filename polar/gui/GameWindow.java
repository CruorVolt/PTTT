package polar.gui;

import polar.game.*;

import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

public class GameWindow implements GameViewer {

	private JFrame frame;
	private Game game;
	private PlayerPanel player_one_panel, player_two_panel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameWindow window = new GameWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GameWindow() {
		game = new Game("Player1", "Player2", this);
		initialize(game);
	}

	private void initialize(Game game) {
		PlayerPanel[] players = choosePlayers();
		player_one_panel = players[0];
		player_two_panel = players[1];

		frame = new JFrame("Polar Tic-Tac-Toe");
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(600,400));
		frame.setBounds(100, 100, 1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.5, 0.5};
		gridBagLayout.rowWeights = new double[]{1.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		try {
			GridPanel game_panel = new GridPanel(game);
			game_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_game_panel = new GridBagConstraints();
			gbc_game_panel.insets = new Insets(0, 0, 5, 0);
			gbc_game_panel.fill = GridBagConstraints.BOTH;
			gbc_game_panel.gridx = 0;
			gbc_game_panel.gridy = 0;
			frame.getContentPane().add(game_panel, gbc_game_panel);
		} catch (BadCoordinateException e) {
			System.out.println("Could not construct PolarCoordinates correctly");
		}
		

		player_one_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_player_one_panel = new GridBagConstraints();
		gbc_player_one_panel.insets = new Insets(0, 0, 5, 0);
		gbc_player_one_panel.fill = GridBagConstraints.BOTH;
		gbc_player_one_panel.gridx = 1;
		gbc_player_one_panel.gridy = 0;
		frame.getContentPane().add(player_one_panel, gbc_player_one_panel);

		player_two_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_player_two_panel = new GridBagConstraints();
		gbc_player_two_panel.insets = new Insets(0, 0, 5, 0);
		gbc_player_two_panel.fill = GridBagConstraints.BOTH;
		gbc_player_two_panel.gridx = 2;
		gbc_player_two_panel.gridy = 0;
		frame.getContentPane().add(player_two_panel, gbc_player_two_panel);
	}
	
	private PlayerPanel[] choosePlayers() {
		
		int i = 0;
		String[][] players = new String[2][Player.PlayerTypes.values().length];
		for (Player.PlayerTypes type : Player.PlayerTypes.values()) {
			players[0][i] = type.string;
			players[1][i] = type.string;
		}
		String[] defaults = { players[0][0], players[1][0] };
		String[] choices = ListDialog.showDialog(null, null, "Who is Playing?", "Choose Players", players, defaults, null);

		switch (choices[0]) {
		case "Human":
		//case Player.PlayerTypes.HUMAN.string:
			player_one_panel = new HumanPlayerPanel(game, 'X');
			System.out.println("Player 1: Human");
			break;
		default:
			System.out.println("No Player of that type found!");
		}

		switch (choices[1]) {
		case "Human":
		//case Player.PlayerTypes.HUMAN.string:
			player_two_panel = new HumanPlayerPanel(game, 'O');
			System.out.println("Player 2: Human");
			break;
		default:
			System.out.println("No Player of that type found!");
		}
		PlayerPanel[] panels = {player_one_panel, player_two_panel};
		return panels;
	}

	@Override
	public void notifyMove(PolarCoordinate coord, boolean turn) {
		player_one_panel.update();
		player_two_panel.update();
	}

	@Override
	public void notifyWin(boolean turn, PolarCoordinate[] winState) {
		// TODO Auto-generated method stub
		
	}

}
