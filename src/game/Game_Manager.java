package game;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math.*;
import java.awt.Point;

public class Game_Manager {
	public static final int BOARD_SIZE = 4;
	public static final int NUM_PIECES = BOARD_SIZE * BOARD_SIZE;

	private byte game_board[][]; 	// game_board[x][y]
	private Point blank_pos; 		// Position of blank

	private Deque<Point> history; 	// Log of moves user makes 

	public Game_Manager() {
		// Validate game constants
		if (NUM_PIECES > Byte.MAX_VALUE) {
			System.out.println("Error: Board size too large!");
			System.exit(0);
		}

		// Initialize Game Variables
		game_board = new byte[BOARD_SIZE][BOARD_SIZE];
		blank_pos  = new Point(0, 0);
		history = new ArrayDeque<Point>();

		// Start new game
		startNewGame();
	}

	public void startNewGame() {
		initializeBoard();
		history.clear();

		shuffleBoard(75);
	}
	
	private void initializeBoard() {
		// Initialize Board Pieces
		byte tile_num = 1; 
		for (int y = 0; y < BOARD_SIZE; y++) 		// Warning: This may trigger many cache misses. 
			for (int x = 0; x < BOARD_SIZE; x++)  	// Look away now. You have been warned. =)
				game_board[x][y] = (tile_num < NUM_PIECES) ? tile_num++:-1;
		
		// Initialize Blank Tile Tracker
		blank_pos.x = BOARD_SIZE - 1; blank_pos.y = BOARD_SIZE - 1; // Initial pos is last element of array x,y
	}

	/*
	* Function: printBoard
	* ----------------------------------------------------
	* Debug method: prints an text representation of
	* the board.
	*/
	private void printBoard() {
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int x = 0; x < BOARD_SIZE; x++) {
				if (game_board[x][y] < 10) 
					System.out.print(game_board[x][y] + "    ");
				else 
					System.out.print(game_board[x][y] + "   ");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public boolean isGameReady() {
		return gameReady;
	}

	/*
	* Function: printValid
	* ----------------------------------------------------
	* Debug method: prints an text representation of
	* valid movies on the board. X is currrent blank.
	* V is a valid move position.
	*/
	private void printValid() {
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int x = 0; x < BOARD_SIZE; x++) {
				char c = (validateMove(x, y))? 'V':'-';
				if (x == blank_pos.x && y == blank_pos.y) System.out.print("X" + " ");
				else System.out.print(c + " ");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	/*
	* Function: getBoardSize
	* ----------------------------------------------------
	* Returns width/length of board. (Useful for generating GUI)
	*/
	public int getBoardSize() {
		return BOARD_SIZE;
	}

	/*
	* Function: getGameBoard
	* ----------------------------------------------------
	* Returns the game board for the interface to show
	* the respective tile numbers.
	*/
	public byte[][] getGameBoard() {
		return game_board;
	}

	/*
	* Function: shuffleBoard
	* ----------------------------------------------------
	* Shuffles the board by randomly moving tiles until a
	* specified depth (difficulty).
	*/
	public void shuffleBoard(int depth) {
		for (int i = 0; i < depth; i++) {
			int direction = ThreadLocalRandom.current().nextInt(0, 5);

			int newX = blank_pos.x; 
			int newY = blank_pos.y;

			switch(direction) {
				case 0: // Left
					newX--;
					break;
				case 1: // Right
					newX++;
					break;
				case 2: // Up
					newY--;
					break;
				case 3: // Down
					newY++;
					break;
			}

			if (validateMove(newX, newY)) 
				makeMove(newX, newY);
			else
				i--; // Try again			
		}
	}

	private void swapTile(int x1, int y1, int x2, int y2) {
		byte temp_tile = game_board[x1][y1];

		game_board[x1][y1] = game_board[x2][y2];
		game_board[x2][y2] = temp_tile;
	}

	/*
	* Function: validateMove
	* ----------------------------------------------------
	* Takes the location of the selected piece and checks
	* if the piece is eligible to move. First check the
	* bounds to see if the piece exists, then see if the 
	* blank piece is adjacent.
	*/
	private boolean validateMove(int x, int y) {
		// Check borders. (Not that this should be possible)
		if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) return false;

		// Is blank adjacent? Is blank directly next to?
		int xDist = Math.abs(x - blank_pos.x);
		int yDist = Math.abs(y - blank_pos.y);

		if (xDist <= 1 && yDist <= 1)
			if ((xDist ^ yDist) == 1)
				return true;

		return false;
	}

	/*
	* Function makeMove
	* ----------------------------------------------------
	* Internal method used to make a raw move in the board.
	* Returns whether the move is successful.
	*/
	private boolean makeMove(int x, int y) {
		if (validateMove(x, y)) {			
			swapTile(x, y, blank_pos.x, blank_pos.y);

			// Update blank tracker
			blank_pos.x = x;
			blank_pos.y = y;

			return true;
		}
		return false;
	}

	public boolean isGameWon() {
		int num = 1;
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int x = 0; x < BOARD_SIZE; x++) {
				if (num != NUM_PIECES) {
					if (game_board[x][y] != num++) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	* Function userMakeMove
	* ----------------------------------------------------
	* External public method used to make a move in
	* the game via a user interface. Also does additional
	* processing like save previous state.
	*/
	public void userMakeMove(int x, int y) {
		// Store the new position of tile (blank's old pos)
		Point new_tile_pos = new Point(blank_pos);

		// Make move
		if (makeMove(x, y)) {
			history.push(new_tile_pos);
		}

		// Check if game over
		if (isGameWon()) {
			System.out.println("Congrats! You won!");
		}
	}

	public boolean undoMove() {
		if (history.size() > 0) {
			Point last = history.pop();
			makeMove(last.x, last.y);
			return true;
		}

		return false;
	}
}