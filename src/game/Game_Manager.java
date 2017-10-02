package game;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math.*;

// TODO: use coordinates instead of x, y variables

public class Game_Manager {
	public static final int BOARD_SIZE = 4;
	public static final int NUM_PIECES = BOARD_SIZE * BOARD_SIZE;

	private byte game_board[][]; // game_board[x][y]
	private int  blankX, blankY; // position of blank

	boolean gameReady = false;

	public Game_Manager() {
		// Validate game constants
		if (NUM_PIECES > Byte.MAX_VALUE) {
			System.out.println("Error: Board size too large!");
			System.exit(0);
		}

		// Initialize Board
		game_board = new byte[BOARD_SIZE][BOARD_SIZE];
		initializeBoard();

		gameReady = true;

		// Test area
		printBoard();
		printValid();
		shuffleBoard(10);
		printBoard();
		printValid();
	}
	
	private void initializeBoard() {
		// Initialize Board Pieces
		byte tile_num = 1; 
		for (int y = 0; y < BOARD_SIZE; y++) 		// Warning: This may trigger many cache misses. 
			for (int x = 0; x < BOARD_SIZE; x++)  	// Look away now. You have been warned. =)
				game_board[x][y] = (tile_num < NUM_PIECES) ? tile_num++:-1;
		
		// Initialize Blank Tile Tracker
		blankX = BOARD_SIZE - 1; blankY = BOARD_SIZE - 1; // Initial pos is last element of array x,y
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
				if (x == blankX && y == blankY) System.out.print("X" + " ");
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

			int newX = blankX; 
			int newY = blankY;

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
		int xDist = Math.abs(x - blankX);
		int yDist = Math.abs(y - blankY);

		if (xDist <= 1 && yDist <= 1)
			if ((xDist ^ yDist) == 1)
				return true;

		return false;
	}

	/*
	* Function makeMove
	* ----------------------------------------------------
	* Internal method used to make a raw move in the board.
	*/
	private void makeMove(int x, int y) {
		if (validateMove(x, y)) {			
			swapTile(x, y, blankX, blankY);

			// Update blank tracker
			blankX = x;
			blankY = y;
		}
	}

	/*
	* Function userMakeMove
	* ----------------------------------------------------
	* External public method used to make a move in
	* the game via a user interface. Also does additional
	* processing like save previous state.
	*/
	public void userMakeMove(int x, int y) {
		// TODO: Save current state

		// Make move
		makeMove(x, y);
	}

	public boolean undoMove() {
		// TODO: Undo move
		return false;
	}
}