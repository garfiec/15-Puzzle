package game;

import java.io.*;
import java.util.*;
import java.awt.Point;

public class Game_Solver {

	private Game_Board original_gameboard;
	private int board_size;

	public Game_Solver(Game_Board board, int board_size) {
		this.board_size = board_size;
		original_gameboard.copyFrom(board);
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
		Set seen = new HashSet();
		Deque<Point> queue = new ArrayDeque<Point>();


	}
}