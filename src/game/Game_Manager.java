package game;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math.*;
import java.awt.Point;
import game.Game_Constants.*;

public class Game_Manager {

	Game_Board game_board = new Game_Board();

	private Deque<Point> history; 	// Log of moves user makes 

	private int move_count;
	private int puzzle_complexity;

	public Game_Manager() {
		// Validate game constants
		if (Game_Constants.NUM_PIECES > Byte.MAX_VALUE) {
			System.out.println("Error: Board size too large!");
			System.exit(0);
		}

		// Initialize Game Variables
		move_count = 0;
		puzzle_complexity = 0;
		history = new ArrayDeque<Point>();

		// Start new game
		startNewGame();
	}

	public void startNewGame() {
		game_board.initializeBoard();
		move_count = 0;
		history.clear();

		// shuffleBoard(12);
		randomizeBoard();
	}
	
	
	/*
	* Function: getBoardSize
	* ----------------------------------------------------
	* Returns width/length of board. (Useful for generating GUI)
	*/
	public int getBoardSize() {
		return Game_Constants.BOARD_SIZE;
	}

	/*
	* Function: getGameBoard
	* ----------------------------------------------------
	* Returns the game board for the interface to show
	* the respective tile numbers.
	*/
	public byte[][] getGameBoard() {
		return game_board.getGameBoard();
	}

	/*
	* Function: shuffleBoard (Deprecated)
	* ----------------------------------------------------
	* Shuffles the board by randomly moving tiles until a
	* specified depth (difficulty).
	*/
	private void shuffleBoard(int depth) {
		for (int i = 0; i < depth; i++) {
			int direction = ThreadLocalRandom.current().nextInt(0, 5);

			int newX = game_board.getBlank().x; 
			int newY = game_board.getBlank().y;

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

			if (game_board.validateMove(newX, newY)) 
				game_board.makeMove(newX, newY);
			else
				i--; // Try again			
		}
	}

	/*
	* Function: randomizeBoard
	* ----------------------------------------------------
	* Generates a random board configurations until a
	* solvable config is found.
	*/
	private void randomizeBoard() {
		do {
			ArrayList<Byte> tiles = new ArrayList<Byte>();
			for (byte i = 1; i < Game_Constants.NUM_PIECES; i++) 
				tiles.add(i);

			Collections.shuffle(tiles, new Random(System.nanoTime()));

			int tileNum = 0;
			for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
				for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
					game_board.setTile(x, y, (tileNum == Game_Constants.NUM_PIECES-1) ? -1:tiles.get(tileNum++));
				}
			}
			puzzle_complexity = computePuzzleComplexity();
		} while (puzzle_complexity % 2 == 1);

	}

	public int getPuzzleComplexity() {
		return puzzle_complexity;
	}

	/*
	* Function: computePuzzleComplexity
	* ----------------------------------------------------
	* Computes and returns a score for the puzzle complexity.
	*/
	private int computePuzzleComplexity() {
		int[] list = new int[Game_Constants.NUM_PIECES];
		int invertCt = 0;

		// Put tile arrangement into array
		int tile_index = 0;
		for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
			for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
				list[tile_index++] = (game_board.getTile(x, y) == -1) ? Game_Constants.NUM_PIECES:game_board.getTile(x, y);
			}
		}

		// Count num inversions
		invertCt += list[0] - 1; // First piece is guarenteed to have every number below
		for (int i = 0; i < Game_Constants.NUM_PIECES; i++) {
			for(int j = i; j < Game_Constants.NUM_PIECES; j++) {
				if (list[j] < list[i]) invertCt++;
			}
		}

		return invertCt;
	}

	public int getMoveCount() {
		return move_count;
	}

	public boolean isGameWon() {
		int num = 1;
		for (int y = 0; y < Game_Constants.BOARD_SIZE; y++) {
			for (int x = 0; x < Game_Constants.BOARD_SIZE; x++) {
				if (num != Game_Constants.NUM_PIECES) {
					if (game_board.getTile(x, y) != num++) {
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
		Point new_tile_pos = new Point(game_board.getBlank());

		// Make move
		if (game_board.makeMove(x, y)) {
			history.push(new_tile_pos);
			move_count++;
		}

		// Check if game over
		if (isGameWon()) {
			System.out.println("Congrats! You won!");
		}
	}

	public boolean undoMove() {
		if (history.size() > 0) {
			Point last = history.pop();
			game_board.makeMove(last.x, last.y);
			return true;
		}

		return false;
	}
}