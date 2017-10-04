package game;

import java.io.*;
import java.util.*;
import java.awt.Point;

public class Game_Solver {

	private Game_Manager game_manager;

	public Game_Solver(Game_Manager gm) {
		game_manager = gm;
	}

	/*
	* Function: solve
	* ----------------------------------------------------
	* Performs a breadth-first-search on puzzle and returns
	* first known solution (Shortest path). 
	* Pair<current config, index of last config>
	*/
	public Deque<Game_Board> solve() {
		// Return/Other Vars
		Game_Board original_gameboard = new Game_Board(game_manager.getGameBoard());
		Deque<Game_Board> solution_path = new ArrayDeque<Game_Board>();

		// BFS Inititalization
		ArrayList<Pair<Game_Board, Integer>> queue = new ArrayList<Pair<Game_Board, Integer>>();
		ArrayList<Game_Board> seen_configs = new ArrayList<Game_Board>();
		int index = 0;
		boolean solutionFound = false;

		// Base case
		queue.add(new Pair<Game_Board, Integer>(original_gameboard, -1));
		seen_configs.add(original_gameboard);

		while (index < queue.size()) {
			Game_Board current = queue.get(index).getLeft();

			// Let game manager determine what is considered solved
			if (game_manager.isSolved(current)) {
				solutionFound = true;
				break;
			}
			else {
				// Try to move in all direction
				Game_Board left = new Game_Board(current);
				Game_Board right = new Game_Board(current);
				Game_Board up = new Game_Board(current);
				Game_Board down = new Game_Board(current);

				if (left.makeMove(left.getBlank().x - 1, left.getBlank().y)) {
					boolean found = false;
					for (int i = 0; i < seen_configs.size(); i++) {
						if (left.compareTo(seen_configs.get(i))) found = true;
					}

					if (found == false) {
						queue.add(new Pair<Game_Board, Integer>(left, index));
						seen_configs.add(left);
					}
				}
				if (right.makeMove(right.getBlank().x + 1, right.getBlank().y)) {
					boolean found = false;
					for (int i = 0; i < seen_configs.size(); i++) {
						if (right.compareTo(seen_configs.get(i))) found = true;
					}

					if (found == false) {
						queue.add(new Pair<Game_Board, Integer>(right, index));
						seen_configs.add(right);
					}
				}
				if (up.makeMove(up.getBlank().x, up.getBlank().y - 1)) {
					boolean found = false;
					for (int i = 0; i < seen_configs.size(); i++) {
						if (up.compareTo(seen_configs.get(i))) found = true;
					}

					if (found == false) {
						queue.add(new Pair<Game_Board, Integer>(up, index));
						seen_configs.add(up);
					}
				}
				if (down.makeMove(down.getBlank().x, down.getBlank().y + 1)) {
					boolean found = false;
					for (int i = 0; i < seen_configs.size(); i++) {
						if (down.compareTo(seen_configs.get(i))) found = true;
					}

					if (found == false) {
						queue.add(new Pair<Game_Board, Integer>(down, index));
						seen_configs.add(down);
					}
				}

			}
			index++;
		}

		if (solutionFound) {
			System.out.println("Solution found!");
			// Start with last item, follow the path down.
			int i = index;
			while (i != -1) {
				solution_path.push(queue.get(i).getLeft());
				i = queue.get(i).getRight();
			}
		}
		else {
			System.out.println("Solution not found");
		}

		return solution_path;
	}

	// Pair class as suggested on stack overflow
	// https://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples
	public class Pair<L, R> {
		private final L left;
		private final R right;

		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}

		public L getLeft() {
			return left;
		}

		public R getRight() {
			return right;
		}
	}
}