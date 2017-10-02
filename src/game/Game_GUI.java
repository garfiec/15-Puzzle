package game;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game_GUI extends JFrame {
	Game_Manager game_manager; //Keep

	private JButton plainButton;
	private JButton label;

	private JLabel outfield;

	private JPanel panel; //Keep
	private JPanel gameBoardGrid; // Keep

	private JButton gameBttnCtrls[][]; //keep



	private int count;

	private JMenuBar menuBar;
	JMenu menu, submenu, fileMenu;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	private int board_size; //keep

	// Set up GUI
	public Game_GUI(Game_Manager gm) {
		super("15 Tiles by Garfie Chiu");

		game_manager = gm;

		board_size = game_manager.getBoardSize();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create UI
		createMenuBar();
		createGameUI();

		// Show initial board setting
		updateBoard();

		setSize( 300, 300 );
		setVisible( true );

	} 


	private void createMenuBar() {
		MenuButtonHandler menuBttnHndlr = new MenuButtonHandler();
		menuBar = new JMenuBar();
		
		// Vars
		JMenu menu;
		JMenuItem menuItem;

		// 1. File Menu
		menu = new JMenu("File");
		// menu.setMnemonic(KeyEvent.VK_B);
		menu.getAccessibleContext().setAccessibleDescription("");
		menuBar.add(menu);

		// 1.1 File Menu: Exit
		menuItem = new JMenuItem("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menu.add(menuItem);		

		// 1.2 File Menu: Exit
		menuItem = new JMenuItem("Reset");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menu.add(menuItem);		

		menu.addSeparator();

		// 1.3 File Menu: Exit
		menuItem = new JMenuItem("Exit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);		

		// 2. Help Menu
		menu = new JMenu("Help");
		// menu.setMnemonic(KeyEvent.VK_C);
		// menu.getAccessibleContext().setAccessibleDescription("");
		menuBar.add(menu);

		// 2.1 Help Menu: How to Play
		menuItem = new JMenuItem("How to Play", KeyEvent.VK_A);
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		// 2.2 Help Menu: About
		menuItem = new JMenuItem("About");
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		this.setJMenuBar(menuBar);
	}

	private void createGameUI() {
		gameBoardGrid = new JPanel();
		gameBoardGrid.setBackground(Color.white);
		gameBoardGrid.setLayout(new GridLayout(board_size, board_size, 3, 3));

		// Initialize Buttons
		gameBttnCtrls = new JButton[board_size][board_size];

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {

				JPanel tile_pnl = new JPanel();
				tile_pnl.setLayout(new BorderLayout(0, 0));
				tile_pnl.setBackground(Color.black);
				gameBoardGrid.add(tile_pnl, BorderLayout.CENTER);
				
				JButton bttn = new JButton();
				tile_pnl.add(bttn, BorderLayout.CENTER);

				gameBttnCtrls[j][i] = bttn;
			}
		}

		getContentPane().add(gameBoardGrid, BorderLayout.CENTER);
	}

	private void updateBoard() {
		byte game_board[][] = game_manager.getGameBoard();

		for (int x = 0; x < board_size; x++) {
			for (int y = 0; y < board_size; y++) {
				if (game_board[x][y] == -1)
					gameBttnCtrls[x][y].setText("");
				else 
					gameBttnCtrls[x][y].setText(Integer.toString(game_board[x][y]));
			}
		}
	}


	// inner class for button event handling
	private class ButtonHandler implements ActionListener {

		// private int count = 1;

		// handle button event
		public void actionPerformed( ActionEvent event ) {
			JOptionPane.showMessageDialog( null,
			                               "You pressed: " + event.getActionCommand() + " " + count);

			outfield.setText ("Count Value: " + count);

			count++;
		}

	} // end private inner class ButtonHandler

	private class MenuButtonHandler implements ActionListener {
		private void showHowTo() {
			updateBoard();
			JOptionPane.showMessageDialog(null, "Test");
		}

		public void actionPerformed(ActionEvent event) {
			showHowTo();
		}
	}



}