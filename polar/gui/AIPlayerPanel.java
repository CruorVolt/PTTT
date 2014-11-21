package polar.gui;

import javax.swing.border.BevelBorder;
import javax.swing.JLabel;

import polar.game.*;

public class AIPlayerPanel extends PlayerPanel{

//maximum search depth reached, 
//number of nodes evaluated, 
//time taken to decide on a move 

	private static final long serialVersionUID = 1L;
	private JLabel searchDepthLabel, nodesLabel, timeLabel;
	
	public AIPlayerPanel(Game game, boolean player){
		super(game, player);
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		playerLabel.setText("AI " + playerLabel.getText());

		searchDepthLabel = new JLabel("Search Depth");
		nodesLabel = new JLabel("Nodes Examined");
		timeLabel = new JLabel("Time Elapsed");

		add(searchDepthLabel);
		add(nodesLabel);
		add(timeLabel);
	}

	@Override
	public void update(PolarCoordinate coord, boolean turn) {
		super.update(coord, turn);
	}
	
}
