package polar.gui;

import javax.swing.border.BevelBorder;
import javax.swing.JLabel;

import polar.game.*;

public class AIPlayerPanel extends PlayerPanel{

//maximum search depth reached, 
//number of nodes evaluated, 
//time taken to decide on a move 

	private static final long serialVersionUID = 1L;
	private JLabel searchDepthLabel, nodesLabel, timeLabel, valueLabel;
	
	public AIPlayerPanel(Game game, boolean player, String descrip){
		super(game, player);
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		playerLabel.setText("<html>AI " + playerLabel.getText() + "<br> Using " + descrip + "</html>");

		searchDepthLabel = new JLabel("Search Depth:", JLabel.CENTER);
		nodesLabel = new JLabel("Nodes Examined:", JLabel.CENTER);
		timeLabel = new JLabel("Time Elapsed:", JLabel.CENTER);
		valueLabel = new JLabel("Value:", JLabel.CENTER);

		add(searchDepthLabel);
		add(nodesLabel);
		add(valueLabel);
		add(timeLabel);
	}

	@Override
	public void update(MoveReport report) {
		super.update(report);
		boolean turn = report.getMove().getPlayer();
		if (turn == this.player) {
			searchDepthLabel.setText("Search Depth: " + report.getDepth() + " plys");
			nodesLabel.setText("Nodes Examined: " + report.getNodes());
			valueLabel.setText("Value: " + report.getValue());
			timeLabel.setText("Time Elapsed: " + (report.getTime() / 1000.0) + " s");
		}
	}
	
}
