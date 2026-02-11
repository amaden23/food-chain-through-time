package gui;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic.GameEngine;
import model.ApexPredator;
import model.Entity;
import model.Food;
import model.Grid;
import model.Predator;
import model.Prey;
/**
 * Graphical representation of game grid.
 * GamePanel Class handles mouse clicks, draws grid cells, animals and food items.
 */
public class GamePanel extends JPanel {
	private GameEngine engine;
	private static final long serialVersionUID = 1L;
	private Map<String, Image> ikonlar; 
	
	
	/**
	 * Constructer for game panel.
	 * Handles mouse clicks.
	 * Calls processTurnWhenCliked() with clicked coordinates.
	 * If turns are successfully happens, updates infos by updateLabels() method.
	 * Locks clicking if game is over and shows Game Over message tab.
	 */
	public GamePanel() {
		//this.engine = engine;
		this.ikonlar = new HashMap<>();
		
		setBackground(Color.WHITE);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();
				
				int rows = engine.getGrid().getRows();
				int cols = engine.getGrid().getCols();
				
				int cellWidth = getWidth() / cols;
				int cellHeight = getHeight() / rows;
				
				int clickedCol = mouseX / cellWidth;
				int clickedRow = mouseY / cellHeight;  
								
				boolean turnSuccessfull = engine.processTurnWhenCliked(clickedCol, clickedRow);
				
				if (turnSuccessfull) {
					System.out.println(clickedCol + ". col and " + clickedRow + ". row is clicked.");
					repaint();
					
					Window w = SwingUtilities.getWindowAncestor(GamePanel.this);
					if (w instanceof MainFrame) {
						((MainFrame) w).updateLabels();
					}
					
					if (engine.isGameOver()) {
						String result = engine.getGameResult();
						
						// Game over penceresini çıkar
						JOptionPane.showMessageDialog(GamePanel.this, result, "Game Over", JOptionPane.INFORMATION_MESSAGE);
					}	
				}	
			}
		});
	}
	
	/**
	 * Calls drawGrid and paintAvailableCells for repainting.
	 * paintComponent() method is used to draw custom graphics. 
	 * Called automatically by Swing whenever the screen needs repainting.
	 * @param g The Graphics context used for drawing operations.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (engine != null) {
			int cols = engine.getGrid().getCols();
			int rows = engine.getGrid().getRows();
			int width = getWidth();
			int height = getHeight();
			
			int cellWidth = width / cols;
			int cellHeight = height / rows;
			
			drawGrid(g);
			paintAvailableCells(g, cellWidth, cellHeight);
			
		}
		else {
			g.drawString("Oyun daha başlamadı.", 50, 50);
		}
	}
	
	
	
	/**
	 * Draws each cell of the grid.
	 * Puts icon of each entity on the grid.
	 * @param g The Graphics context used for drawing operations.
	 */
	private void drawGrid(Graphics g) {
		Grid grid = engine.getGrid();
		int rows = grid.getRows();
		int cols = grid.getCols();
		int panelWidth = this.getWidth();
		int panelHeight = this.getHeight();
		
		int cellWidth = panelWidth / cols;
		int cellHeight = panelHeight / rows;
		
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				
				Entity e = grid.getEntity(x, y);	
				
				int cellX = x*cellWidth;
				int cellY = y*cellHeight;
				
				// draw grid
				g.setColor(Color.BLACK);
				g.drawRect(cellX, cellY, cellWidth, cellHeight); 
				
				// put icons
				if (e != null) {
					Image img = getEntityImage(e.getName());
					g.drawImage(img, cellX + 2, cellY + 2, cellWidth - 4, cellHeight - 4, null);
				}
				
				
				// KARAKTER BOYAMA KODLARI
				/*
				if (e == null) {
					g.setColor(Color.WHITE);
				}
				else if (e instanceof Predator) {
					g.setColor(Color.ORANGE); 
				}
				else if (e instanceof Prey) {
					g.setColor(Color.CYAN);
				}
				else if (e instanceof ApexPredator) {
					g.setColor(Color.RED);
				}
				else if (e instanceof Food) {
					g.setColor(Color.GREEN);
				}
				
				// cell'i boya
				g.fillRect(cellX, cellY, cellWidth, cellHeight);
				// cell'in çerçevesini boya
				g.setColor(Color.BLACK);
				g.drawRect(cellX, cellY, cellWidth, cellHeight);
				
				// harf de ekle şimdilik !!!!!!!!! daha sonra karakterleri ekle
				if (e != null) {
					g.setColor(Color.BLACK);
					g.drawString(e.getSymbol(), cellX + cellWidth/2, cellY + cellHeight/2);
				}*/
							
			}
		}
				
		
	}
	
	
	
	/**
	 * Paints available cells to move for player(predator).
	 * @param g The Graphics context.
	 * @param cellWidth The cell width.
	 * @param cellHeight The cell height.
	 */
	private void paintAvailableCells(Graphics g, int cellWidth, int cellHeight) {
		if (engine != null) {
			Predator player = engine.getPredatorPlayer();		
			if (player == null) {
				return;
			}
			
			List<int[]> moves = player.getAvailableMoves(engine.getGrid());
			
			int mevcutX = player.getX();
			int mevcutY = player.getY();
			
			for (int[] m : moves) {
				int possibleX = m[0];
				int possibleY = m[1]; 
				
				double a = Math.pow(possibleX-mevcutX, 2);
				double b = Math.pow(possibleY-mevcutY, 2);
				
				double oyuncuyaMesafesi = Math.sqrt(a + b);
				
				if (oyuncuyaMesafesi > Math.sqrt(2)) {
					g.setColor(new Color(255, 215, 0, 150));
				}
				else {
					g.setColor(new Color(144, 238, 144, 150));
				}
				
				g.fillRect(possibleX * cellWidth, possibleY * cellHeight, cellWidth, cellHeight);				
			}	
		}
	}
	
	
	
	/**
	 * Repaints game screen after load.
	 */
	public void refreshAfterLoad() {
		this.repaint();
		this.requestFocusInWindow();
	}
	
	
	/**
	 * Returns the icon of the entity with given name.
	 * @param nameOfEntity Name of entity to give its icon.
	 * @return Returns the icon of entity.
	 */
	private Image getEntityImage(String nameOfEntity) {
		String filename = nameOfEntity.toLowerCase() + ".png";
		
		if (ikonlar.containsKey(filename)) {
			return ikonlar.get(filename);
		}
		
		
		try {
			File path = new File("ikonlar/" + filename);
			if (path.exists()) {
				Image img = ImageIO.read(path);
				ikonlar.put(filename, img);
				return img;
			}
			else {
				System.out.print(filename +" PATH'İNİ BULAMADI BİR ŞEYLER YANLIL!!!!!!");
			}
			
		}
		catch (IOException e) {
			System.out.println("İKONU YÜKLEYEMEDİ BİR ŞEYLER YANLIŞ");
		}
		
		return null;   
		
		
	}
	
	
	
	// getter-setters
	/**
	 * Sets engine and repaints the grid.
	 * @param engine The game engine.
	 */
	public void setEngine(GameEngine engine) {
		this.engine = engine;
		repaint();	
	}
	
	public GameEngine getGameEngine() {
		return this.engine;
	}
	
	

}






