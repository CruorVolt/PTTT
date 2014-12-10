package polar.gui;

import polar.game.*;
import polar.game.exceptions.BadCoordinateException;
import polar.game.styles.*;

import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

import logic.TD.TD;

public class GameWindow implements GameViewer, ActionListener {

	private JFrame frame;
	private Game game;
	private GridPanel game_panel;
	private PlayerPanel player_one_panel, player_two_panel;
	private JButton resetButton;

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
		gridBagLayout.rowHeights = new int[] {1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.4, 0.4};
		gridBagLayout.rowWeights = new double[]{2.0, 0.1};
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
		gbc_player_one_panel.gridheight = 2;
		frame.getContentPane().add(player_one_panel, gbc_player_one_panel);

		player_two_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_player_two_panel = new GridBagConstraints();
		gbc_player_two_panel.insets = new Insets(0, 0, 5, 0);
		gbc_player_two_panel.fill = GridBagConstraints.BOTH;
		gbc_player_two_panel.gridx = 2;
		gbc_player_two_panel.gridy = 0;
		gbc_player_two_panel.gridheight = 2;
		frame.getContentPane().add(player_two_panel, gbc_player_two_panel);
		
		resetButton = new JButton("Reset Game");
		resetButton.addActionListener(this);
		GridBagConstraints gbc_reset = new GridBagConstraints();
		gbc_reset.insets = new Insets(0, 0, 5, 0);
		gbc_reset.gridx = 0;
		gbc_reset.gridy = 1;
		frame.getContentPane().add(resetButton, gbc_reset);
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
		TD td = null;
		
		switch (choices[0]) {
		case "Human":
			player_one_panel = new HumanPlayerPanel(game, Player.PLAYER_X);
			style1 = HumanPlayStyle.getInstance();
			break;
		case "Random":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Random Selection");
			style1 = new RandomPlayStyle();
			break;
		case "Greedy Heuristic":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Greedy Heuristic");
			style1 = new GreedyPlayStyle(Player.PLAYER_X, game);
			break;
		case "Minimax Search (3 plys)":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Minimax (3 plys)");
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, false, 2);
			break;
		case "Minimax Search (4 plys)":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Minimax (4 plys)");
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, false, 3);
			break;
		case "Minimax Search (5 plys)":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Minimax (5 plys)");
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, false, 4);
			break;
		case "Alpha-Beta Pruning (3 plys)":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Alpa-Beta Pruning (3 plys)");
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, true, 2);
			break;
		case "Alpha-Beta Pruning (4 plys)":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Alpa-Beta Pruning (4 plys)");
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, true, 3);
			break;
		case "Alpha-Beta Pruning (5 plys)":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Alpa-Beta Pruning (5 plys)");
			style1 = new SearchPlayStyle(Player.PLAYER_X, game, true, 4);
			break;
		case "Temporal Difference":
<<<<<<< HEAD
			td = new TD(Player.PLAYER_X, "TDweights.txt");
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X);
=======
			td = new TD(Player.PLAYER_X, "./src/TDweights.txt");
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Temporal Difference Learning");
>>>>>>> origin/master
			style1 = new DifferencePlayStyle(game, td, Player.PLAYER_X);
			break;
		case "Decision Tree Classification":
			player_one_panel = new AIPlayerPanel(game, Player.PLAYER_X, "Decision Tree Classifier");
			style1 = new ClassifierPlayStyle(Player.PLAYER_X, game);
			break;
		default:
			System.out.println("No Player of type "+choices[0]+ " found!");
			break;
		}

		switch (choices[1]) {
		case "Human":
			player_two_panel = new HumanPlayerPanel(game, Player.PLAYER_O);
			style2 = HumanPlayStyle.getInstance();
			break;
		case "Random":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Random Selection");
			style2 = new RandomPlayStyle();
			break;
		case "Greedy Heuristic":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Greedy Heuristic");
			style2 = new GreedyPlayStyle(Player.PLAYER_O, game);
			break;
		case "Minimax Search (3 plys)":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Minimax (3 plys)");
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, false, 2);
			break;
		case "Minimax Search (4 plys)":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Minimax (4 plys)");
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, false, 3);
			break;
		case "Minimax Search (5 plys)":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Minimax (5 plys)");
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, false, 4);
			break;
		case "Alpha-Beta Pruning (3 plys)":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Alpa-Beta Pruning (3 plys)");
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, true, 2);
			break;
		case "Alpha-Beta Pruning (4 plys)":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Alpa-Beta Pruning (4 plys)");
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, true, 3);
			break;
		case "Alpha-Beta Pruning (5 plys)":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Alpa-Beta Pruning (5 plys)");
			style2 = new SearchPlayStyle(Player.PLAYER_O, game, true, 4);
			break;
		case "Temporal Difference":
			if(td==null) {
<<<<<<< HEAD
				td = new TD(Player.PLAYER_X, "TDweights.txt");
				player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O);
=======
				td = new TD(Player.PLAYER_X, "./src/TDweights.txt");
				player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Temporal Difference Learning");
>>>>>>> origin/master
				style2 = new DifferencePlayStyle(game, td, Player.PLAYER_O);
			}
			// to use a single TD net for both players -- must use min playstyle 
			else {
				player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Temporal Difference Learning");
				style2 = new TDMinPlayStyle(game, td, Player.PLAYER_O);
			}
			break;
		case "Decision Tree Classification":
			player_two_panel = new AIPlayerPanel(game, Player.PLAYER_O, "Decision Tree Classifier");
			style2 = new ClassifierPlayStyle(Player.PLAYER_O, game);
			break;
		default:
			System.out.println("No Player of type "+choices[1]+ " found!");
			break;
		}
		game.setPlayStyles(style1, style2);
		PlayerPanel[] panels = {player_one_panel, player_two_panel};
		return panels;
	}
	
	@Override
	public void notifyMove(MoveReport report) {
		player_one_panel.update(report);
		player_two_panel.update(report);
		game_panel.update(report);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		start.restart();
	}

}
