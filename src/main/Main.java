package main;


import javax.swing.SwingUtilities;
import gui.MainFrame;

/**
 * Main class for whole project.
 * Responsible for launching the game and initializing the GUI.
 */
public class Main {
	
	/**
	 * The main method that starts the game.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
	}
	
	
}


