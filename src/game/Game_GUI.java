package game;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class Game_GUI extends JFrame {
	Game_Manager game_manager; 

	private JPanel panel; 
	private JPanel gameBoardGrid; 

	private JButton gameBttnCtrls[][]; 

	private JMenuBar menuBar;

	private int board_size; 

	// Set up GUI
	public Game_GUI(Game_Manager gm) {
		super("15 Tiles by Garfie Chiu");

		game_manager = gm;

		board_size = game_manager.getBoardSize();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create UI
		createMenuBar();
		createGameUI();

		// Wait until game is ready to be played
		while (!game_manager.isGameReady()) {}

		// Show initial board setting
		updateBoard();

		setSize(500, 500);
		setVisible(true);

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
		menuBar.add(menu);

		// 1.1 File Menu: Undo
		menuItem = new JMenuItem("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Undo last move");
		menuItem.setActionCommand("Undo");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);		

		// 1.2 File Menu: Undo
		menuItem = new JMenuItem("Undo All");
		menuItem.setActionCommand("Undo All");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);	

		menu.addSeparator();

		// 1.3 File Menu: Reset
		menuItem = new JMenuItem("Reset");
		menuItem.getAccessibleContext().setAccessibleDescription("Starts a new game");
		menuItem.setActionCommand("Reset");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);		

		menu.addSeparator();

		// 1.4 File Menu: Solve
		menuItem = new JMenuItem("Solve");
		menuItem.setActionCommand("Solve");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);	

		menu.addSeparator();

		// 1.5 File Menu: Exit
		menuItem = new JMenuItem("Exit");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		// menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.setActionCommand("Exit");
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
		menuItem.setActionCommand("Tutorial");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		// 2.2 Help Menu: About
		menuItem = new JMenuItem("About");
		menuItem.setActionCommand("About");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		this.setJMenuBar(menuBar);
	}

	private void createGameUI() {
		GameButtonHandler gameBttnHndlr = new GameButtonHandler();

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
				bttn.setActionCommand(Integer.toString((i*board_size) + j));
				bttn.addActionListener(gameBttnHndlr);
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

	// Game Interface
	private class GameButtonHandler implements ActionListener {
		public void actionPerformed( ActionEvent event ) {
			int bttnID = Integer.parseInt(event.getActionCommand());

			int x = bttnID % board_size;
			int y = bttnID / board_size;

			game_manager.userMakeMove(x, y);
			updateBoard();
		}

	} 

	private class MenuButtonHandler implements ActionListener {
		private void showHowTo() {
			// TODO: Add how to note
			JOptionPane.showMessageDialog(null, "1. Lorem ipsum dolar sit amet");
		}

		private void showAbout() {
			// TODO: Add about message
			JOptionPane.showMessageDialog(null, "2. Lorem ipsum dolar sit amet");
		}

		// Undo each move with 1 second delay for visualization
		private void undoAllMoves() {
			// while (game_manager.undoMove()) {	

			// }
			Timer t = new Timer(500, new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (!game_manager.undoMove()) {
						((Timer)event.getSource()).stop();
					}
					updateBoard();
				}
			});
			t.start();
		}

		public void actionPerformed(ActionEvent event) {
			switch(event.getActionCommand()) {
				case "Undo":
					if (!game_manager.undoMove())
						JOptionPane.showMessageDialog(null, "Nothing to undo!");
					updateBoard();
					break;
				case "Undo All":
					System.out.println("Undo all!");
					undoAllMoves();
					break;
				case "Reset":
					game_manager.startNewGame();
					updateBoard();
					break;
				case "Solve":
					// TODO: Solve
					System.out.println("Solve");
					break;
				case "Exit":
					System.out.println("Goodbye.");
					System.exit(0);
					break;
				case "Tutorial":
					showHowTo();
					break;
				case "About":
					showAbout();
					break;
			}
		}
	}
}