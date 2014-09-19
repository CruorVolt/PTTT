package polar;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.BevelBorder;

public class GameWindow {

	private JFrame frame;

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
		initialize();
	}

	private void initialize() {
		frame = new JFrame("Polar Tic-Tac-Toe");
		frame.setResizable(true);
		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.5, 0.5};
		gridBagLayout.rowWeights = new double[]{1.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		GridPanel game_panel = new GridPanel();
		game_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_game_panel = new GridBagConstraints();
		gbc_game_panel.insets = new Insets(0, 0, 5, 0);
		gbc_game_panel.fill = GridBagConstraints.BOTH;
		gbc_game_panel.gridx = 0;
		gbc_game_panel.gridy = 0;
		frame.getContentPane().add(game_panel, gbc_game_panel);
		
		JPanel player_one_panel = new JPanel();
		player_one_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_player_one_panel = new GridBagConstraints();
		gbc_player_one_panel.insets = new Insets(0, 0, 5, 0);
		gbc_player_one_panel.fill = GridBagConstraints.BOTH;
		gbc_player_one_panel.gridx = 1;
		gbc_player_one_panel.gridy = 0;
		frame.getContentPane().add(player_one_panel, gbc_player_one_panel);

		JPanel player_two_panel = new JPanel();
		player_two_panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_player_two_panel = new GridBagConstraints();
		gbc_player_two_panel.insets = new Insets(0, 0, 5, 0);
		gbc_player_two_panel.fill = GridBagConstraints.BOTH;
		gbc_player_two_panel.gridx = 2;
		gbc_player_two_panel.gridy = 0;
		frame.getContentPane().add(player_two_panel, gbc_player_two_panel);
	}

}
