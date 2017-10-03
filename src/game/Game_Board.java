package game;

import java.io.*;
import java.util.*;
import java.awt.Point;
import game.Game_Constants.*;

public class Game_Board {
	private byte game_board[][]; 	// game_board[x][y]
	private Point blank_pos; 		// Position of blanks

	public Game_Board() {
		game_board = new byte[Game_Constants.BOARD_SIZE][Game_Constants.BOARD_SIZE];
		blank_pos  = new Point(0, 0);
	}

	/*
	* Function: copyFrom
	* ----------------------------------------------------
	* Performs a deep copy of Game_Board object to current
	* instance.
	*/
	public void copyFrom(Game_Board original) {
		for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
			for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
				game_board[x][y] = original.getTile(x, y);
			}
		}

		blank_pos = new Point(original.getBlank());
	}

	public void initializeBoard() {
		// Initialize Board Pieces
		byte tile_num = 1; 
		for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) 		// Warning: This may trigger many cache misses. 
			for (int x = 0; x < Game_Constants.BOARD_SIZE; x++)  	// Look away now. You have been warned. =)
				game_board[x][y] = (tile_num < Game_Constants.NUM_PIECES) ? tile_num++:-1;
		
		// Initialize Blank Tile Tracker
		blank_pos.x = Game_Constants.BOARD_SIZE - 1; blank_pos.y = Game_Constants.BOARD_SIZE - 1; // Initial pos is last element of array x,y
	}

	/*
	* Function: printBoard
	* ----------------------------------------------------
	* Debug method: prints an text representation of
	* the board.
	*/
	public void printBoard() {
		for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
			for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
				if (game_board[x][y] < 10) 
					System.out.print(game_board[x][y] + "    ");
				else 
					System.out.print(game_board[x][y] + "   ");
			}
			System.out.println("");
		}
		System.out.println("");
	}


	/*
	* Function: printValid
	* ----------------------------------------------------
	* Debug method: prints an text representation of
	* valid movies on the board. X is currrent blank.
	* V is a valid move position.
	*/
	public void printValid() {
		for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
			for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
				char c = (validateMove(x, y))? 'V':'-';
				if (x == blank_pos.x && y == blank_pos.y) System.out.print("X" + " ");
				else System.out.print(c + " ");
			}
			System.out.println("");
		}
		System.out.println("");
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

	public void setTile(int x, int y, byte val) {
		game_board[x][y] = val;
	}

	public byte getTile(int x, int y) {
		return game_board[x][y];
	}

	public Point getBlank() {
		return blank_pos;
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
	public boolean validateMove(int x, int y) {
		// Check borders. (Not that this should be possible)
		if (x < 0 || x >= Game_Constants.BOARD_SIZE || y < 0 || y >= Game_Constants.BOARD_SIZE) return false;

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
	public boolean makeMove(int x, int y) {
		if (validateMove(x, y)) {			
			swapTile(x, y, blank_pos.x, blank_pos.y);

			// Update blank tracker
			blank_pos.x = x;
			blank_pos.y = y;

			return true;
		}
		return false;
	}


}