package game;

import java.io.*;
import java.util.*;
import java.awt.Point;

public class Game_Solver {

	private byte original_gameboard[][];
	private int board_size;

	public Game_Solver(byte[][] board, int board_size) {
		this.board_size = board_size;
		original_gameboard = makeBoardCopy(board);
	}

	/*
	* Function: makeBoardCopy
	* ----------------------------------------------------
	* Performs a deep copy of the game board.
	*/
	private byte[][] makeBoardCopy(byte[][] original) {
		byte copy[][] = new byte[board_size][board_size];
		for (int x = 0; x < board_size; x++) {
			for (int y = 0; y < board_size; y++) {
				copy[x][y] = original[x][y];
			}
		}
		return copy;
	}

	/*
	* Function: findShortestPath
	* ----------------------------------------------------
	* Performs a breadth-first-search on puzzle and returns
	* first known solution. This simulates moves via the 
	* real game board so we must restore the original board
	* before replaying the shortest route.
	*/
	private void findShortestPath() {


		Deque<Point> queue = new ArrayDeque<Point>();

	}
}