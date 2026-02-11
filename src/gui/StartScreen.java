package gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
/**
 * Represents the start panel where users choose game settings.
 */
public class StartScreen extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtRounds;
	private MainFrame mainFrame;
	
	/**
	 * Creates the start screen and takes user's start preferences.
	 * When user clicks "Start Game" button, calls startGamePlayScreen() method and switches to game play screen.
	 */
	public StartScreen(MainFrame mainFrame) {
		setBackground(new Color(180, 180, 180));
		this.mainFrame = mainFrame;
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Food Chain Through Time");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblNewLabel.setBounds(74, 0, 272, 23);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Select Era:");
		lblNewLabel_1.setBounds(33, 72, 96, 13);
		add(lblNewLabel_1);
		
		JComboBox comboBoxEra = new JComboBox();
		comboBoxEra.setModel(new DefaultComboBoxModel(new String[] {"", "Past", "Present", "Future"}));
		comboBoxEra.setSelectedIndex(0);
		comboBoxEra.setBounds(139, 68, 110, 21);
		add(comboBoxEra);
		
		JLabel lblNewLabel_2 = new JLabel("Select Grid Size:");
		lblNewLabel_2.setBounds(33, 129, 96, 13);
		add(lblNewLabel_2);
		
		JComboBox comboBoxGridSize = new JComboBox();
		comboBoxGridSize.setModel(new DefaultComboBoxModel(new String[] {"", "10x10", "15x15", "20x20"}));
		comboBoxEra.setSelectedIndex(0);
		comboBoxGridSize.setBounds(139, 125, 110, 21);
		add(comboBoxGridSize);
		
		JLabel lblNewLabel_3 = new JLabel("Total Rounds:");
		lblNewLabel_3.setBounds(33, 183, 103, 13);
		add(lblNewLabel_3);
		
		txtRounds = new JTextField();
		txtRounds.setBounds(139, 180, 110, 19);
		add(txtRounds);
		txtRounds.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("(Please enter at least 10.)");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 8));
		lblNewLabel_4.setBounds(255, 183, 145, 13);
		add(lblNewLabel_4);
		
		
		
		JButton btnStart = new JButton("Start Game");
		
		// "Start Game" butonununa basınca mainFrame'e geçerek oyunu başlatır.
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxEra.getSelectedIndex() == 0) {
					JOptionPane.showMessageDialog(null, "Please select an era.");
					return;
				}
				if (comboBoxGridSize.getSelectedIndex() == 0) {
					JOptionPane.showMessageDialog(null, "Please select a grid size.");
					return;
				}
				try {
					String selectedEra = (String) comboBoxEra.getSelectedItem();
					
					String sizeStr = (String) comboBoxGridSize.getSelectedItem();
					int gridSize = Integer.parseInt(sizeStr.split("x")[0]);
					
					int raunds = Integer.parseInt(txtRounds.getText().trim()); 
					if (raunds < 10) {
						JOptionPane.showMessageDialog(null, "The game should last at least 10 raunds !");	
						return;
					}
					
					
					mainFrame.setSize(1100, 650);
					mainFrame.setLocationRelativeTo(null); 
					mainFrame.startGamePlayScreen(selectedEra, gridSize, raunds);
					
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Please enter a valid number for rounds."); 
				}
			}
		});
		btnStart.setBounds(139, 233, 110, 21);
		add(btnStart);
		
		JLabel lblNewLabel_5 = new JLabel("Try Not To Starve !");
		lblNewLabel_5.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblNewLabel_5.setBounds(125, 22, 166, 23);
		add(lblNewLabel_5);
		
	}
}
