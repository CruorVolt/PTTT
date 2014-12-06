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

		searchDepthLabel = new JLabel("Search Depth:", JLabel.CENTER);
		nodesLabel = new JLabel("Nodes Examined:", JLabel.CENTER);
		timeLabel = new JLabel("Time Elapsed:", JLabel.CENTER);

		add(searchDepthLabel);
		add(nodesLabel);
		add(timeLabel);
	}

	@Override
	public void update(MoveReport report) {
		super.update(report);
		boolean turn = report.getMove().getPlayer();
		if (turn == this.player) {
			searchDepthLabel.setText("Search Depth: " + report.getDepth() + " plys");
			//nodesLabel = new JLabel("Nodes Examined: " + report.getNodes());
			timeLabel.setText("Time Elapsed: " + (report.getTime() / 1000.0) + " s");
		}
	}
	
}
