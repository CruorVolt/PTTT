package polar.gui;

import polar.game.Game;
import polar.game.BadCoordinateException;

import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class GameWindow {

	private JFrame frame;
	private Game game;
	private JPanel player_one_panel, player_two_panel;

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
		game = new Game("Player1", "Player2");
		initialize(game);
	}

	private void initialize(Game game) {
		String[] players = choosePlayers();

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
		
		switch (players[0]) {
		case "Human":
			player_one_panel = new HumanPlayerPanel(game);
			break;
		default:
			System.out.println("No Player of that type found!");
		}

		switch (players[1]) {
		case "Human":
			player_two_panel = new HumanPlayerPanel(game);
			break;
		default:
			System.out.println("No Player of that type found!");
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
	
	private String[] choosePlayers() {
		String[][] players = { {"Human"}, {"Human"}};
		String[] defaults = {"Human", "Human"};
		String[] choices = ListDialog.showDialog(null, null, "Who is Playing?", "Choose Players", players, defaults, null);
		return choices;
	}

}
