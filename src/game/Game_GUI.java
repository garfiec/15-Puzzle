package game;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game_GUI extends JFrame {
	Game_Manager game_manager;

	private JButton plainButton;
	private JButton label;

	private JLabel outfield;
	private JPanel nestedPanel;

	private int count;

	private JMenuBar menuBar;
	JMenu menu, submenu, fileMenu;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;


	// set up GUI
	public Game_GUI(Game_Manager game_manager) {
		super("15 Tiles by Garfie Chiu");

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		// getContentPane().setLayout(new BorderLayout());
		getContentPane().setLayout(new GridLayout(4, 4));

		// Create Menu
		createMenuBar();

		/*Example button code*/
		count = 10;

		ButtonHandler bh1 = new ButtonHandler() ;

		plainButton = new JButton ("Click Here");
		getContentPane().add (plainButton, BorderLayout.WEST );
		plainButton.addActionListener ( bh1 );

		label = new JButton ("Default Text");
		getContentPane().add (label, BorderLayout.CENTER );
		label.addActionListener ( new ButtonHandler() );

		// code to create this output field
		outfield = new JLabel ("Default text");
		getContentPane().add (outfield, BorderLayout.SOUTH );
		outfield.setText ("Some other Text");

		nestedPanel = new JPanel( new GridLayout ( 2, 3, 5, 5 ), false );
		for (int i = 1; i <= 6 ; i++) {
			JLabel lab = new JLabel ( "Label " + i );
			nestedPanel.add ( lab );
		}

		getContentPane().add (nestedPanel, BorderLayout.NORTH );

		setSize( 300, 250 );
		setVisible( true );

	} 


	private void createMenuBar() {
		MenuButtonHandler menuBttnHndlr = new MenuButtonHandler();
		menuBar = new JMenuBar();
		
		// Vars
		JMenu menu;
		JMenuItem menuItem;

		// 1. File Menu
		menu = new JMenu("File");
		// menu.setMnemonic(KeyEvent.VK_B);
		menu.getAccessibleContext().setAccessibleDescription("");
		menuBar.add(menu);

		// 1.1 File Menu: Exit
		menuItem = new JMenuItem("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menu.add(menuItem);		

		// 1.2 File Menu: Exit
		menuItem = new JMenuItem("Reset");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menu.add(menuItem);		

		menu.addSeparator();

		// 1.3 File Menu: Exit
		menuItem = new JMenuItem("Exit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);		

		// 2. Help Menu
		menu = new JMenu("Help");
		// menu.setMnemonic(KeyEvent.VK_C);
		// menu.getAccessibleContext().setAccessibleDescription("");
		menuBar.add(menu);

		// 2.1 Help Menu: How to Play
		menuItem = new JMenuItem("How to Play", KeyEvent.VK_A);
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		// 2.2 Help Menu: About
		menuItem = new JMenuItem("About");
		menuItem.getAccessibleContext().setAccessibleDescription("");
		menuItem.addActionListener(menuBttnHndlr);
		menu.add(menuItem);

		this.setJMenuBar(menuBar);
	}



	// inner class for button event handling
	private class ButtonHandler implements ActionListener {

		// private int count = 1;

		// handle button event
		public void actionPerformed( ActionEvent event ) {
			JOptionPane.showMessageDialog( null,
			                               "You pressed: " + event.getActionCommand() + " " + count);

			outfield.setText ("Count Value: " + count);

			count++;
		}

	} // end private inner class ButtonHandler

	private class MenuButtonHandler implements ActionListener {
		private void showHowTo() {
			JOptionPane.showMessageDialog(null, "Test");
		}

		public void actionPerformed(ActionEvent event) {
			showHowTo();
			System.out.println("Got here");
		}
	}



}