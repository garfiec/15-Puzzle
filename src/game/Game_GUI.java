package game;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import game.Game_Constants.*;

public class Game_GUI extends JFrame {
	Game_Manager game_manager; 

	// Solver
	Game_Solver solver;
	SolverThread st = new SolverThread();
	Thread tr = new Thread(st);
	Deque<Game_Board> solution;

	private JPanel panel; 
	private JPanel gameBoardGrid; 

	private JButton gameBttnCtrls[][]; 

	private JMenuBar menuBar;

	// Set up GUI
	public Game_GUI(Game_Manager gm) {
		super(Game_Constants.GUI_TITLE);

		game_manager = gm;
		solver = new Game_Solver(game_manager);
		if (Game_Constants.SOLVER_RETROACTIVE) {
			System.out.println("Solving (ahead of time)...");
			tr.start(); 
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create UI
		createMenuBar();
		createGameUI();

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
		menuItem.setActionCommand("Exit");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);		 

		// 2. Help Menu
		menu = new JMenu("Help");
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
		gameBoardGrid.setLayout(new GridLayout(Game_Constants.BOARD_SIZE, Game_Constants.BOARD_SIZE, 3, 3));

		// Initialize Buttons
		gameBttnCtrls = new JButton[Game_Constants.BOARD_SIZE][Game_Constants.BOARD_SIZE];

		for (int i = 0; i < Game_Constants.BOARD_SIZE; i++) {
			for (int j = 0; j < Game_Constants.BOARD_SIZE; j++) {

				JPanel tile_pnl = new JPanel();
				tile_pnl.setLayout(new BorderLayout(0, 0));
				tile_pnl.setBackground(Color.black);
				gameBoardGrid.add(tile_pnl, BorderLayout.CENTER);
				
				JButton bttn = new JButton();
				bttn.setActionCommand(Integer.toString((i*Game_Constants.BOARD_SIZE) + j));
				bttn.addActionListener(gameBttnHndlr);
				tile_pnl.add(bttn, BorderLayout.CENTER);

				gameBttnCtrls[j][i] = bttn;
			}
		}

		getContentPane().add(gameBoardGrid, BorderLayout.CENTER);
	}

	private void updateBoard() {
		byte game_board[][] = game_manager.getGameBoard().getMatrix();

		for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
			for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
				if (game_board[x][y] == -1)
					gameBttnCtrls[x][y].setText("");
				else 
					gameBttnCtrls[x][y].setText(Integer.toString(game_board[x][y]));
			}
		}
	}

	public class SolverThread implements Runnable {
		public boolean solution_available = false;

		@Override
		public void run() {
			solution = solver.solve();
			solution_available = true;
		}
	}

	// Game Interface
	private class GameButtonHandler implements ActionListener {
		public void actionPerformed( ActionEvent event ) {
			int bttnID = Integer.parseInt(event.getActionCommand());

			int x = bttnID % Game_Constants.BOARD_SIZE;
			int y = bttnID / Game_Constants.BOARD_SIZE;

			game_manager.userMakeMove(x, y);
			updateBoard();
			
			if (game_manager.isSolved()) {
				int moves = game_manager.getMoveCount();
				int complexity = game_manager.getPuzzleComplexity();
				JOptionPane.showMessageDialog(null, "Congrats! You won!\n " + 
													"You took " + moves + " moves.\n "+
													"Puzzle complexity: " + complexity);
			}
		}

	} 

	private class MenuButtonHandler implements ActionListener {
		private void showHowTo() {
			// TODO: Add how to note
			JOptionPane.showMessageDialog(null, Game_Constants.HOWTO_TEXT);
		}

		private void showAbout() {
			JOptionPane.showMessageDialog(null, Game_Constants.ABOUT_TEXT);
		}

		// Undo each move with delay for visualization
		private void undoAllMoves() {
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

		private void showSolution() {
			if (!Game_Constants.SOLVER_RETROACTIVE) {
				System.out.println("Solving...");
				tr.start(); 
			}

			Timer ti = new Timer(500, new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (st.solution_available) {
						if (solution.size() > 0) {
							game_manager.setGameBoard(solution.pop());
							updateBoard();
						}
						else {
							((Timer)event.getSource()).stop();
						}
					}
				}
			});
			ti.start();
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
					showSolution();
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