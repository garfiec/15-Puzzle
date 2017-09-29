package game;

import java.io.*;
import java.util.*;

public class Game {
	public static void main(String[] arg) {
		System.out.println("Starting game...");

		// Creates instance of Game_Manager and pass to gui. 
		// Game_Manager handles all the game logic.
		Game_Manager game_manager = new Game_Manager();
		Game_GUI 	 game_gui 	  = new Game_GUI(game_manager);
		
		System.out.println("Game running...");
	}
}
