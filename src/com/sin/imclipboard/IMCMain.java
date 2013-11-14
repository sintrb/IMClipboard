package com.sin.imclipboard;

/**
 * Entrance of IMClipboard
 * 
 * @author RobinTang
 * 
 */
public class IMCMain {

	/**
	 * Version of this application
	 * 
	 * @1.2 Add auto resize 2013.11.14
	 * 
	 * @V1.1 First version 2013.11.05
	 */
	public static String VERSION = "1.2";
	public static String DATE = "2013.11.14";

	public static void main(String[] args) {
		System.out.println("IMClipboard V" + VERSION + " " + DATE);
		new MainFrame().setVisible(true);
	}
}
