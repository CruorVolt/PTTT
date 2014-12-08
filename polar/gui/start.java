package polar.gui;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

import polar.game.Game;
import polar.gui.GameWindow;

public class start {

	public static void main(String[] args) {
		
		final Game game = new Game();

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
	
	/*
	* Restart the entire application: workaround for EvenQueue and Game locks not allowing 
	* straightforward game resetting. This flushes all current queues and creates an
	* entirely new instance.
	*/
	public static void restart() {
		try {
			//Get the location of the java command for this system
			String java = System.getProperty("java.home") + "/bin/java";
			List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
			StringBuffer vmArgsOneLine = new StringBuffer();
			for (String arg : vmArguments) {
				//Agents need special treatment, this shouldn't matter here
				if (!arg.contains("-agentlib")) {
					vmArgsOneLine.append(arg);
					vmArgsOneLine.append(" ");
				}
			}

			//Initial command is system's java location plus args
			final StringBuffer cmd = new StringBuffer(java + " " + vmArgsOneLine);

			//Windows file path style
			//final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine); 
		 	
			//Add the location of start.main
			String[] mainCommand = System.getProperty("sun.java.command").split(" "); //Not VM-universal

			if (mainCommand[0].endsWith(".jar")) { //We're starting from a compiled .jar
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else { //We're starting from a .class file
				cmd.append("-cp " + System.getProperty("java.class.path") + " " + mainCommand[0]);

				//Windows file path style
				//cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
			
			//Dispose of everything before restarting
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			System.exit(0);

		} catch (Exception o) {
			System.out.println("ERROR in restart:");
			o.printStackTrace();
		}
	}
}
