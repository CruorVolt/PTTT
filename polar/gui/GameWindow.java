package polar.gui;

import polar.game.*;

import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

public class GameWindow implements GameViewer {

	private JFrame frame;
	private Game game;
	private GridPanel game_panel;
	private PlayerPanel player_one_panel, player_two_panel;

	public GameWindow(Game game) {
		this.game = game;
		initialize(game);
		frame.setVisible(true);
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
		gridBagLayout.columnWeights = new double[]{1.0, 0.4, 0.4};
		gridBagLayout.rowWeights = new double[]{1.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		try {
			game_panel = new GridPanel(game);
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
			i++;
		}
		String[] defaults = { players[0][0], players[1][0] };
		String[] choices = ListDialog.showDialog(null, null, "Who is Playing?", "Choose Players", players, defaults, null);
		PlayStyle style1 = null;
		PlayStyle style2 = null;
		
		switch (choices[0]) {
		case "Human":
			player_one_panel = new HumanPlayerPanel(game, Player.PLAYER_X);
			style1 = HumanPlayStyle.getInstance();
			break;
		case "Random":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X);
			style1 = new RandomPlayStyle();
			break;
		case "Greedy Heuristic":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X);
			style1 = new GreedyPlayStyle(Player.PLAYER_X, game);
			break;
		case "Minimax Search":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X);
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, false);
			break;
		default:
			System.out.println("No Player of that type found!");
		}

		switch (choices[1]) {
		case "Human":
			player_two_panel = new HumanPlayerPanel(game, Player.PLAYER_O);
			style2 = HumanPlayStyle.getInstance();
			break;
		case "Random":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O);
			style2 = new RandomPlayStyle();
			break;
		case "Greedy Heuristic":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O);
			style2 = new GreedyPlayStyle(Player.PLAYER_O, game);
			break;
		case "Minimax Search":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O);
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, false);
			break;
		default:
			System.out.println("No Player of that type found!");
		}
		game.setPlayStyles(style1, style2);
		PlayerPanel[] panels = {player_one_panel, player_two_panel};
		return panels;
	}

	@Override
	public void notifyMove(PolarCoordinate coord, boolean turn) {
		player_one_panel.update(coord, turn);
		player_two_panel.update(coord, turn);
		game_panel.update(coord);
	}

	@Override
	public void notifyWin(boolean turn, Move[] winState) {
		if (winState == null) {
			//Game over with no winner
			//game.end();
		} else {
			game_panel.updateWin(winState);
			//game.end();
		}
	}

}
