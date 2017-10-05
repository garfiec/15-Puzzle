package game;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import game.Game_Constants.*;

public class Game_GUI extends JFrame {
	private Game_Manager game_manager;

	private boolean useImage;

	// Solver
	Game_Solver solver;
	SolverThread st = new SolverThread();
	Thread tr = new Thread(st);
	Deque<Game_Board> solution;

	private JPanel panel; 
	private JPanel gameBoardGrid; 
	private JPanel statusBar;

	private JLabel gameStatus;

	private JButton gameBttnCtrls[][]; 
	private BufferedImage subImages[];

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
		createStatusBar();

		useImage = false;
		subImages = new BufferedImage[Game_Constants.NUM_PIECES + 1];

		// Show initial board setting
		
		updateBoard();
		updateUI();

		setResizable(false);
		setSize(Game_Constants.GUI_WIDTH, Game_Constants.GUI_HEIGHT);
		setVisible(true);

	} 

	private File findImage() {
		final JFileChooser dialog = new JFileChooser();
		dialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = dialog.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile();
		}

		return null;
	}

	private void loadImage(File file) {
		// No file
		if (file == null) return;

		BufferedImage importedImage = null;
		try {
			importedImage = ImageIO.read(file);
			if (importedImage == null) {
				System.out.println("Error loading image file");
				return;
			}
		}
		catch (IOException e) {
			System.out.println("Error loading image file.");
			return;	
		}
		useImage = true;

		// Resize
		Image tmp = importedImage.getScaledInstance((int) (Game_Constants.GUI_WIDTH * 1.1), (int) (Game_Constants.GUI_HEIGHT * 1.1), BufferedImage.SCALE_FAST);
		BufferedImage resizedImage = new BufferedImage((int)(Game_Constants.GUI_WIDTH * 1.1), (int) (Game_Constants.GUI_HEIGHT * 1.1), BufferedImage.TYPE_INT_RGB);
		resizedImage.getGraphics().drawImage(tmp, 0, 0, null);

		int imageX = resizedImage.getWidth();
		int imageY = resizedImage.getHeight();

		int chunkX = (int) (imageX / 4);
		int chunkY = (int) (imageY / 4);

		// Save image chunks
		int i = 1; 
		for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
			for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
				subImages[i++] = resizedImage.getSubimage(x*chunkX, y*chunkY, chunkX, chunkY);
			}
		}
			

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

		// 1.1 File Menu: New Game
		menuItem = new JMenuItem("New Game");
		menuItem.getAccessibleContext().setAccessibleDescription("Starts a new game");
		menuItem.setActionCommand("New Game");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);				

		menu.addSeparator();

		// 1.2 File Menu: Select Image
		menuItem = new JMenuItem("Select Image");
		menuItem.setActionCommand("Select Image");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);	

		// 1.3 File Menu: Remove Image
		menuItem = new JMenuItem("Remove Image");
		menuItem.setActionCommand("Remove Image");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);	

		menu.addSeparator();

		// 1.3 File Menu: Exit
		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("Exit");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem); 

		// 2. Edit Menu
		menu = new JMenu("Tools");
		menuBar.add(menu);

		// 2.1 File Menu: Undo
		menuItem = new JMenuItem("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Undo last move");
		menuItem.setActionCommand("Undo");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);		

		// 2.2 File Menu: Undo
		menuItem = new JMenuItem("Undo All");
		menuItem.setActionCommand("Undo All");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);	

		// 3. Tools Menu
		menu = new JMenu("Tools");
		menuBar.add(menu);

		// 3.4 File Menu: Solve
		menuItem = new JMenuItem("Solve");
		menuItem.setActionCommand("Solve");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);	

		// 4. Help Menu
		menu = new JMenu("Help");
		menuBar.add(menu);

		// 4.1 Help Menu: How to Play
		menuItem = new JMenuItem("How to Play", KeyEvent.VK_A);
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.setActionCommand("Tutorial");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		// 4.2 Help Menu: About
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
		gameBoardGrid.setLayout(new GridLayout(Game_Constants.BOARD_SIZE, Game_Constants.BOARD_SIZE, 5, 5));
		gameBoardGrid.setBorder(new EmptyBorder(5,5,5,5));

		// Initialize Buttons
		gameBttnCtrls = new JButton[Game_Constants.BOARD_SIZE][Game_Constants.BOARD_SIZE];

		for (int i = 0; i < Game_Constants.BOARD_SIZE; i++) {
			for (int j = 0; j < Game_Constants.BOARD_SIZE; j++) {

				JPanel tile_pnl = new JPanel();
				tile_pnl.setLayout(new BorderLayout(0, 0));
				gameBoardGrid.add(tile_pnl, BorderLayout.CENTER);
				
				JButton bttn = new JButton();
				bttn.setFocusPainted(false);
				// bttn.setBackground(Color.LIGHT_GRAY);
				bttn.setBackground(new Color(214, 230, 255));
				bttn.setActionCommand(Integer.toString((i*Game_Constants.BOARD_SIZE) + j));
				bttn.addActionListener(gameBttnHndlr);
				tile_pnl.add(bttn, BorderLayout.CENTER);

				gameBttnCtrls[j][i] = bttn;
			}
		}

		getContentPane().add(gameBoardGrid, BorderLayout.CENTER);
	}

	private void createStatusBar() {
		statusBar = new JPanel();
		statusBar.setBorder(new BevelBorder(BevelBorder.RAISED));
		gameStatus = new JLabel();
		gameStatus.setText("");
		statusBar.add(gameStatus);

		getContentPane().add(statusBar, BorderLayout.SOUTH);
	}

	private void updateBoard() {
		byte game_board[][] = game_manager.getGameBoard().getMatrix();

		for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
			for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
				if (game_board[x][y] == -1) {
					gameBttnCtrls[x][y].setText("");
					gameBttnCtrls[x][y].setVisible(false);
					if (useImage) {
						if (game_manager.isSolved()) {
							gameBttnCtrls[x][y].setIcon(new ImageIcon(subImages[Game_Constants.NUM_PIECES]));
							gameBttnCtrls[x][y].setVisible(true);
						}
					}
					else {
						gameBttnCtrls[x][y].setIcon(null);
					}
				}
				else {
					gameBttnCtrls[x][y].setText(Integer.toString(game_board[x][y]));
					gameBttnCtrls[x][y].setVisible(true);
					if (useImage) {
						gameBttnCtrls[x][y].setIcon(new ImageIcon(subImages[game_board[x][y]]));
					}
					else {
						gameBttnCtrls[x][y].setIcon(null);
					}
				}
			}
		}
	}

	private void updateUI() {
		gameStatus.setText("Game complexity: " + game_manager.getPuzzleComplexity() + " | Move count: " + game_manager.getMoveCount());
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
			updateUI();
			
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
					updateUI();
				}
			});
			t.start();
		}

		private void showSolution() {
			JOptionPane.showMessageDialog(null, Game_Constants.SSOLUTION_TEXT);
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
				case "New Game":
					game_manager.startNewGame();
					updateBoard();
					updateUI();
					break;
				case "Select Image":
					loadImage(findImage());
					updateBoard();
					break;
				case "Remove Image":
					useImage = false;
					updateBoard();
					break;
				case "Exit":
					System.out.println("Goodbye.");
					System.exit(0);
					break;
				case "Undo":
					if (!game_manager.undoMove())
						JOptionPane.showMessageDialog(null, "Nothing to undo!");
						updateBoard();
					break;
				case "Undo All":
					System.out.println("Undo all!");
					undoAllMoves();
					break;
				case "Solve":
					showSolution();
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