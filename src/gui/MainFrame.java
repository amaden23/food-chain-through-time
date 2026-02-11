package gui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import logic.GameEngine;
import model.Animal;
import model.ApexPredator;
import model.Predator;
import model.Prey;
import util.FileManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Main class for GUI operations.
 * Holds the game panel and information panels.
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JPanel gameContainerPanel;
	
	private StartScreen startScreen;
	private GameEngine engine;
	private GamePanel gamePanel;
	
	private JLabel labelRoundInfo;
	private JLabel labelApexInfo;
	private JLabel labelPredatorInfo;
	private JLabel labelPreyInfo;
	
	
	/**
	 * Constructs the MainFrame and initializes the main GUI components.
	 * Sets up the CardLayout to manage switching between Start Screen and Game Screen.
	 */
	public MainFrame() {
		
		setTitle("Food Chain Through Time");	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 370);	
		setLocationRelativeTo(null);
		
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		
		startScreen = new StartScreen(this);
		
		createGameInterface();
		
		mainPanel.add(startScreen, "START");
		mainPanel.add(gameContainerPanel, "GAME");
		setContentPane(mainPanel);
		cardLayout.show(mainPanel, "START");
		
	}
	
	
	
	/**
	 * Creates the main game interface.
	 * Holds game panel and information panels.
	 */
	private void createGameInterface() {
		gameContainerPanel = new JPanel();
		gameContainerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		gameContainerPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton btnSave = new JButton("Save Game");
		JButton btnLoad = new JButton("Load Game");	
		JButton btnExit = new JButton("Exit");
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (engine == null) {  
					JOptionPane.showMessageDialog(MainFrame.this, "Game has not started yet.", "Save Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					FileManager.saveGame(engine);
					JOptionPane.showMessageDialog(MainFrame.this, "Game saved successfully.", "Save Game", JOptionPane.INFORMATION_MESSAGE);
				} 
				catch (Exception ex) {
					JOptionPane.showMessageDialog(MainFrame.this, "Error saving game: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (engine == null) {
					JOptionPane.showMessageDialog(MainFrame.this, "Please start a game first to initialize the engine.", "Load Error", JOptionPane.WARNING_MESSAGE);
					return;	
				}
				try {
					FileManager.loadGame(engine); 
					gamePanel.repaint();
					updateLabels();
					gamePanel.requestFocusInWindow();
					JOptionPane.showMessageDialog(MainFrame.this, "Game loaded successfully.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
				} 
				catch (Exception ex) {
					JOptionPane.showMessageDialog(MainFrame.this, "Error loading game: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		topButtonPanel.add(btnSave);
		topButtonPanel.add(btnLoad);
		topButtonPanel.add(btnExit);
		
		gameContainerPanel.add(topButtonPanel, BorderLayout.NORTH);
		
		gamePanel = new GamePanel();
		//gameContainerPanel.add(gamePanel, BorderLayout.CENTER);
		
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		
		JLabel titleLabel = new JLabel("<html> FOOD CHAIN THROUGH TIME: TRY NOT TO STARVE!"
										+ "<br>"
										+ "<br><html>", SwingConstants.CENTER);
		
		titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		
		wrapperPanel.add(titleLabel, BorderLayout.NORTH);
		wrapperPanel.add(gamePanel, BorderLayout.CENTER);
		wrapperPanel.setBorder(new EmptyBorder(20, 30, 30, 30));
		
		
		gameContainerPanel.add(wrapperPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 10));
		gameContainerPanel.add(panel, BorderLayout.EAST);
		panel.setLayout(new GridLayout(4, 1, 0, 0));
		
		JPanel panel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_2.setBackground(new Color(210, 210, 210));
		panel.add(panel_2);
		
		labelRoundInfo = new JLabel("Raund: 1 / 10");
		panel_2.add(labelRoundInfo);
		
		JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_1.setBackground(new Color(210, 220,150));
		panel.add(panel_1);
		
		labelApexInfo = new JLabel("Apex Score: 0");
		panel_1.add(labelApexInfo);
		
		JPanel panel_4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_4.setBackground(new Color(210, 150, 150));
		panel.add(panel_4);
		
		labelPredatorInfo = new JLabel("Player Score: 0");
		panel_4.add(labelPredatorInfo);
		
		JPanel panel_3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_3.setBackground(new Color(150, 210, 215));
		panel.add(panel_3);
		
		labelPreyInfo = new JLabel("Prey Score: 0");
		panel_3.add(labelPreyInfo);
		
	}
	
	
	/**
	 * Initializes the game engine with the selected settings and switches the view to the game screen.
	 * @param era The selected game era.
	 * @param gridSize The size of the grid.
	 * @param rounds Total number of raunds to be played.
	 */
	public void startGamePlayScreen(String era, int gridSize, int rounds) {
		System.out.println("MainFrame: Oyun başlatılıyor. Başlatma seçenekleri: " + era + " - " + gridSize + "x" + gridSize);
		
		this.engine = new GameEngine(era, gridSize, rounds);
		gamePanel.setEngine(this.engine);
		
		updateLabels();
		cardLayout.show(mainPanel, "GAME");
		gamePanel.requestFocusInWindow();
		
	}
	
	
	
	/**
	 * As the raunds go on, updates the game informations.
	 */
	public void updateLabels() {
		if (engine == null) return;
		
		int current = engine.getCurrentRaund();
		int total = engine.getTotalRaunds();
		
		// oyun bitti current +1 oluyor, onu düzelt.
		if (current > total) {
			current = total;
		}
		
		labelRoundInfo.setText("<html>ABOUT GAME:" + "<br>"
								+ "<br>"
								+ "	Era: " + engine.getCurrentMode() + "<br>"
								+ "<br>"
								+ "Round: " + current + " / " + total + "<br>"
								+ "<br>"
								+ "Grid Size: " + engine.getGrid().getCols() + "x" + engine.getGrid().getCols()
								+ "<br>");
		
		Animal apex = engine.getAnimalByType(ApexPredator.class);
		Animal predator = engine.getAnimalByType(Predator.class);
		Animal prey = engine.getAnimalByType(Prey.class);	
		
		if (apex != null) {
			labelApexInfo.setText("<html><br>" 
									+ "Apex Predator:  " + apex.getName() + "<br>"
									+  "<br>"
									+ "Score:  " + apex.getScore() + "<br>"
									+ "<br>"
									+ "Cooldown:  " + apex.getAbilityCooldown() + " raunds<html>");
		}
		
		if (predator != null) { 
			labelPredatorInfo.setText("<html><br>" 
									+ "Predator(Player):  "+ predator.getName() + "<br>"
									+ "<br>"
									+ "Score:  " + predator.getScore() + "<br>"
									+ "<br>"
									+ "Cooldown:  " + predator.getAbilityCooldown() + " raunds<html>"); 
		}
		
		
		if (prey != null) {
			labelPreyInfo.setText("<html><br>" 
									+ "Prey:  " + prey.getName() + "<br>"  
									+ "<br>"
									+ "Score:  " + prey.getScore() + "<br>" 
									+ "<br>"
									+ "Cooldown:  " + prey.getAbilityCooldown()+ " raunds<html>");
		}
		
	}
	
	
}

