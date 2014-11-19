package polar.game;

import java.awt.EventQueue;

import polar.gui.GameWindow;

public class start {

	public static void main(String[] args) {
		
		Game game = new Game();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameWindow window = new GameWindow(game);
					game.addViewer(window);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		synchronized(game) {
			try {
				game.wait();
				game.begin();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
