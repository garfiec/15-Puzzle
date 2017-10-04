package game;

public final class Game_Constants {
	/*
	* Solver runs on two modes:
	* 	True: solver will run in the background shortly after puzzle is generated (assuming it will take a while to solve)
	* 	False: solver will only run when the user clicks solve. (Note: This solves the puzzle after )
	*/ 
	public static final boolean SOLVER_RETROACTIVE = true;

	public static final int BOARD_SIZE = 4;
	public static final int NUM_PIECES = BOARD_SIZE * BOARD_SIZE;

	// Strings
	public static final String GUI_TITLE = "15 Tiles by Garfie Chiu";
	public static final String ABOUT_TEXT = "Program by Garfie Chiu for CS342 - Project 2";
	public static final String HOWTO_TEXT = "The objective of the game is to arrange the the tiles in such a way that the numbers are \n" +
											"ordered sequentially from left to right, top to bottom. You can rearrange the numbers by  \n" +
											"clicking on tiles that are directly adjacent to the blank tile.\n\n" +
											"The menu options perform the following: \n" +
											"File > Undo: Reverse your last move. \n" +
											"File > Undo all: Reverses all your moves (with animation). \n" +
											"File > Reset: Generates a new board configuration. \n" +
											"File > Solve: Reveals the solution to the game once computed. \n" +
											"File > Exit: Exits the game. ";
}