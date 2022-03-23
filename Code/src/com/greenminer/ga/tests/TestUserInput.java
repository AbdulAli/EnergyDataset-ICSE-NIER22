package com.greenminer.ga.tests;

import java.util.Scanner;

public class TestUserInput {

	public static void main(String[] args) {
		 // 1. Create a Scanner using the InputStream available.
	    Scanner scanner = new Scanner( System.in );

	    // 2. Don't forget to prompt the user
	    System.out.print( "Type some data for the program: " );

	    // 3. Use the Scanner to read a line of text from the user.
	    String input = scanner.nextLine();

	    // 4. Now, you can do anything with the input string that you need to.
	    // Like, output it to the user.
	    System.out.println( "input = " + Double.parseDouble(input)*3);
	}

}
