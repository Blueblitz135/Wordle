import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class contains all methods, including the main method, to run the game "Wordle" on the console.
 * @author aidan.leung
 * 12.10.2022
 */
public class Main {

	/**
	 * The main method will run the game "Wordle". 
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		String guess;
		String hiddenWord;
		String[] hints = new String[6];
		String[] guesses = new String[6];
		int numOfGuesses;
		int guessesLeft;
		int choice;
		boolean successful;
		String repeat = "yes";
		
		// Greet the user and print instructions
		printTitle();
		printInstructions();
		
		while (repeat.equals("yes")) {	
			// Reset all variables and arrays
			successful = false;
			choice = 0;
			numOfGuesses = 0;
			guess = "";
			for (int i = 0; i < hints.length; i++) {
				// Initialize the hints array with all empty strings
				// This allows for String concatenation later in the program
				hints[i] = "";
			}
			for (int i = 0; i < guesses.length; i++) {
				// Reset the guesses array
				guesses[i] = null;
			}
			
			// Ask user if they want to guess the hidden word or set the word for the computer to guess
			System.out.println("\nChoose a game mode:");
			System.out.println("\n1. Guess a random word generated by the computer");
			System.out.println("\n2. Input a word for the computer to guess");
			
			// Error checking for user input (must be either 1 or 2)
			// try-catch block helps prevent errors when users enter a data type that is not an integer
			while (choice != 1 && choice != 2) {
				try {
					System.out.print("\nEnter '1' or '2': ");
					choice = in.nextInt();
					in.nextLine(); // help advance Scanner
					
					if (choice != 1 && choice != 2) {
						System.out.println("Enter '1' or '2'!");
					}
					
				} catch (Exception e) {
					// Handles potential errors such as InputMismatchExcpetion, where the user doesn't enter an integer
					System.out.println("Enter '1' or '2'!");
					in.nextLine(); // help advance Scanner
				}
			}
			
			
			// Wordle game play based on game mode
			if (choice == 1) {
				System.out.println("\n\nGame mode: Guessing a random word\n");
				hiddenWord = generateRandomWord().toLowerCase(); // Generate random word and convert it to lower case
				
				while (numOfGuesses < 6 && !guess.equalsIgnoreCase(hiddenWord)) {
					// Inform user how many guesses they have left
					guessesLeft = 6 - numOfGuesses;
					if (guessesLeft == 1) {
						System.out.println("You have 1 guess left.");
					} else {
						System.out.println("You have " + guessesLeft + " guesses left.");
					}
					
					
					// Prompt user to enter guess
					System.out.print("Enter your guess: ");
					guess = in.nextLine();
					// Error checking to see if the user's guess is part of the word list
					while (!isAValidWord(guess)) {
						System.out.println("Please enter a five letter word that is part of the word list!");
						System.out.print("Enter your guess: ");
						guess = in.nextLine();
					}
					
					// Give hints and print the current grid
					guesses[numOfGuesses] = guess; // add the current guess to the list of guesses
					giveHints(guess, hiddenWord, hints, numOfGuesses);
					printGrid(hints, guesses);
					
					// If the user has guessed the word, exit the loop
					if (guess.equals(hiddenWord)) {
						successful = true;
					}
					
					numOfGuesses++;
				}
				
				// Congratulate user if they guessed correctly and reveal the hidden word if they didn't 
				if (successful) {
					System.out.println("Congratulations! You guessed the word in " + numOfGuesses + " tries.\n");
				} else {
					System.out.println("The word was " + hiddenWord + ". Better luck next time!\n");
				}
				
				// Update and show statistics and guess distribution
				updateAndShowStats(successful);
				System.out.println();
				updateAndShowGuessDistribution(numOfGuesses);
				
			} else {
				// AI Simulation
				System.out.println("\n\nGame mode: Inputting a word for the computer to guess\n");
				System.out.print("Enter a five-letter word: ");
				hiddenWord = in.nextLine();
				
				// Check if the user's input is a word from the word list
				while (!isAValidWord(hiddenWord)) {
					System.out.println("Enter a word from the word list!");
					System.out.print("Enter a five-letter word: ");
					hiddenWord = in.nextLine();
				}
				
				System.out.println("\nAI simulation:");
				while (numOfGuesses < 6 && !guess.equalsIgnoreCase(hiddenWord)) {
					// Inform user how many guesses the computer has left
					guessesLeft = 6 - numOfGuesses;
					if (guessesLeft == 1) {
						System.out.println("The computer has 1 guess left.");
					} else {
						System.out.println("The computer has " + guessesLeft + " guesses left.");
					}
					
					
					// Let the computer choose a random word
					// There is no need for error checking since the computer will always generate a five-letter word
					guess = generateRandomWord().toLowerCase();
					
					// Give hints and print the current grid
					guesses[numOfGuesses] = guess; // add the current guess to the list of guesses
					giveHints(guess, hiddenWord, hints, numOfGuesses);
					printGrid(hints, guesses);
					
					if (guess.equals(hiddenWord)) {
						successful = true;
					}
	
					numOfGuesses++;
				}
				
				// Tell user if the computer was able to guess the word or not
				if (successful) {
					System.out.println("The computer guessed the word in " + numOfGuesses + " tries! Better luck next time!");
				} else {
					System.out.println("The computer could not guess the word! You win!");
				}
				
			}
			
			// Ask user if they want to play again
			System.out.print("\nDo you want to play again? (enter 'yes' or 'no'): ");
			repeat = in.nextLine();
			// Error checking for user input
			while (!repeat.equalsIgnoreCase("yes") && !repeat.equalsIgnoreCase("no")) {
				System.out.println("Enter 'yes' or 'no'!");
				System.out.print("Enter 'yes' or 'no': ");
				repeat = in.nextLine();
			}
			
		}
		in.close();
	}
	
	/**
	 * This method checks if the letter in the guess is in the correct position relative to the hidden word.
	 * @param guess the user's guess
	 * @param hiddenWord the hidden word that the user is trying to guess
	 * @param index the index of the guess and hidden word that is being checked
	 * @return true if the characters of the guess and hidden word at the specified index are the same, false otherwise
	 */
	public static boolean inCorrectPosition(String guess, String hiddenWord, int index) {
		char guessLetter = guess.charAt(index);
		char hiddenLetter = hiddenWord.charAt(index);
		
		if (guessLetter == hiddenLetter) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method checks if the hidden word contains the specified character of the guess.
	 * @param guessChar a character in the user's guess
	 * @param hiddenWord the hidden word that the user is trying to guess
	 * @return true if the character exists in the hidden word, false otherwise
	 */
	public static boolean containsLetter(char guessChar, String hiddenWord) {
		for (int i = 0; i < hiddenWord.length(); i++) {
			if (guessChar == hiddenWord.charAt(i)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method will read from a file containing a word list, 
	 * store the words in a string array, and return a random word.
	 * @return hiddenWord the word that will be used as the hidden word for the game
	 * @throws FileNotFoundException
	 */
	public static String generateRandomWord() throws FileNotFoundException {
		File targetFile = new File("wordList.txt");
		Scanner read = new Scanner(targetFile);
		int count = 0;
		int index = 0;
		String randomWord;
		
		// Count how many words are in the file
		while (read.hasNext()) {
			read.next();
			count++;
		}
		read.close();
		read = new Scanner(targetFile);
		
		// Declare and populate word list array
		String[] wordList = new String[count];
		for (int i = 0; i < wordList.length; i++) {
			wordList[i] = read.next();
		}
		read.close();
		
		// Generate a random number as the index and choose the word from the the word list array
		index = (int) (Math.random() * count); // generates a number between 0 and (count - 1)
		randomWord = wordList[index];
		
		return randomWord;
	}
	
	/**
	 * This method will print the hints, each with their corresponding guesses.
	 * If the user hasn't used all their guesses yet, it will print empty square for the unused hints.
	 * @param hints the array that has all the hints for the guesses
	 * @param guesses the array that has all the user's guesses
	 */
	public static void printGrid(String[] hints, String[] guesses) {
		final String EMPTY = "\u2B1B"; // a black square indicating an empty row
		
		for (int i = 0; i < hints.length; i++) {
			// If the user has not guessed for the row yet, print empty boxes
			if (!hints[i].equals("")) {
				System.out.println(hints[i]);
			} else {
				for (int j = 0; j < 5; j++) {
					System.out.print(EMPTY + " ");
				}
				System.out.println();
			}
			
			if (!(guesses[i] == null)) {
				// Print the words with 2 spaces between each letter
				// This only executes if the guess is not null
				for (int j = 0; j < guesses[i].length(); j++) {
					System.out.print(guesses[i].charAt(j) + "  ");
				}
			}
			System.out.println();
			System.out.println();
		}
		
	}
	
	/**
	 * This method will evaluate each letter in the current guess and determine whether it is
	 * in the correct position, in the word but not in the correct position, or not in the word at all;
	 * each letter of the guess will be filled with either a check mark, circle, or cross mark respectively.
	 * For duplicate letters in a guess, it will give a circle if there is still another occurrence 
	 * or a cross mark otherwise.
	 * @param guess the user's guess
	 * @param hiddenWord the hidden word that the user is trying to guess
	 * @param hints the array that has all the hints for the user's guesses
	 * @param numOfGuesses the current number of guesses the user has used
	 */
	public static void giveHints(String guess, String hiddenWord, String hints[], int numOfGuesses) {
		final String CORRECT = "\u2714"; // a check mark hint indicating the character is in the correct position
		final String WRONG = "\u274C"; // a cross mark hint indicating the character is not in the word at all
		final String CIRCLE = "\u26AB"; // a circle hint indicating the character is in the word but wrong position
		String[] currentGuessHints = new String[5]; // hints for the current guess
		char[] distinctChar = new char[5];
		int[] numOfOccurrences = new int[5];
		int distinctCharCount = 0;
		
		// Accumulate the number of distinct characters and add them to the distinctChar array
		for (int i = 0; i < hiddenWord.length(); i++) {
			char currentChar = hiddenWord.charAt(i);
			boolean charExists = false;
			
			// Check if the character is already added to the distinctChar array
			for (int j = 0; j < distinctChar.length; j++) {
				if (currentChar == distinctChar[j]) {
					// Add the occurrence count by one if the character already exists in the distinctChar array
					numOfOccurrences[j]++;
					charExists = true;
					break;
				}
			}
			
			if (!charExists) {
				// If the character doesn't exist yet, it is distinct, so add it to the distinctChar array
				distinctChar[distinctCharCount] = currentChar;
				numOfOccurrences[distinctCharCount]++;
				distinctCharCount++;
			}
				
		}
		
		// Make sure the guess is formatted to be all lower case
		guess = guess.toLowerCase();
		
		// Check if any characters of the guess are in the correct position
		for (int i = 0; i < guess.length(); i++) {
			if (inCorrectPosition(guess, hiddenWord, i)) {
				for (int j = 0; j < distinctChar.length; j++) {
					if (guess.charAt(i) == distinctChar[j]) {
						// Subtract the count of the character by one and 
						// give the hint that it is in the correct position (check mark)
						numOfOccurrences[j]--;
						currentGuessHints[i] = CORRECT; 
						break;
					}
				}
			}
		}
		
		// Check each characters of the guess again to determine if they exist in the word or not
		for (int i = 0; i < guess.length(); i++) {
			// If the guess hint is already filled, skip it
			if (currentGuessHints[i] == null) {
				if (containsLetter(guess.charAt(i), hiddenWord)) {
					for (int j = 0; j < distinctCharCount; j++) {
						if (guess.charAt(i) == distinctChar[j] && numOfOccurrences[j] > 0) {
							// If there are still occurrences of the character in the hidden word 
							// and the character has not been checked yet, subtract the count by one and give the circle hint
							numOfOccurrences[j]--;
							currentGuessHints[i] = CIRCLE;
							break;
						} else {
							// If the conditions are not met, give the hint that the character 
							// does not exist in the word (cross mark)
							currentGuessHints[i] = WRONG;
						}
					}
				} else {
					// If the character is not in the word at all, give the cross mark hint
					currentGuessHints[i] = WRONG;
				}
			}
		}
		
		// Add all the current guess hints together to form a single string that will be stored in the hints array
		for (int i = 0; i < currentGuessHints.length; i++) {
			hints[numOfGuesses] += currentGuessHints[i] + " ";
		}
		
	}
	
	/**
	 * This method will read from the the text file that has an ASCII drawing of the word "Wordle".
	 * It will then print it to the console.
	 * @throws FileNotFoundException
	 */
	public static void printTitle() throws FileNotFoundException {
		File targetFile = new File("title.txt");
		Scanner read = new Scanner(targetFile);
		while (read.hasNextLine()) {
			String line = read.nextLine();
			System.out.println(line);
		}
		read.close();	
	}
	
	/**
	 * This method will read from the the text file that has the instructions to the game.
	 * It will then print it to the console.
	 * @throws FileNotFoundException
	 */
	public static void printInstructions() throws FileNotFoundException {
		File targetFile = new File("instructions.txt");
		Scanner read = new Scanner(targetFile);
		while (read.hasNextLine()) {
			String line = read.nextLine();
			System.out.println(line);
		}
		read.close();
	}
	
	/**
	 * This method checks if the user's input is part of the word list.
	 * @param guess the user's guess
	 * @return true if the guess is part of the word list, false otherwise
	 * @throws FileNotFoundException
	 */
	public static boolean isAValidWord(String guess) throws FileNotFoundException {
		File targetFile = new File("wordList.txt");
		Scanner read = new Scanner(targetFile);
		boolean valid = false;
		
		while (read.hasNextLine()) {
			String word = read.nextLine();
			if (guess.equalsIgnoreCase(word)) {
				valid = true;
				read.close();
				return true;
			}
		}
		read.close();
		
		return valid;
		
	}
	
	/**
	 * This method will read from the "guessDistribution.txt" file which has six values, each 
	 * representing the statistics of how many tries they used each time to guess the hidden word. 
	 * If the user guessed correctly this time, it will update the current guess distribution. 
	 * It will then print the guess distribution to the console.
	 * @param numOfGuesses the number of guesses it took for the user to guess the hidden word. If this value is equal
	 * to 6, it means that the user did not guess the hidden word 
	 * @throws FileNotFoundException
	 */
	public static void updateAndShowGuessDistribution(int numOfGuesses) throws FileNotFoundException {
		File targetFile = new File("guessDistribution.txt");
		Scanner read = new Scanner(targetFile);
		int[] guessDistribution = new int[6];
		
		// Read from file and store the distribution of the guesses
		for (int i = 0; i < guessDistribution.length; i++) {
			guessDistribution[i] = read.nextInt(); 
		}
		read.close();
		
		// Add one to the guess distribution of the number of tries the user used
		// If the user couldn't guess the word, don't do anything
		if (numOfGuesses < 6) {
			guessDistribution[numOfGuesses - 1]++;
		}
		
		// Write the updated values of the guess distribution back to the file
		PrintWriter write = new PrintWriter(targetFile);
		for (int i = 0; i < guessDistribution.length; i++) {
			write.println(guessDistribution[i]);
		}
		write.close();
		
		// Display guess distribution
		System.out.println("Guess Distribution (statstics on how many tries it took for you to guess the hidden word):");
		System.out.println("1 guess: " + guessDistribution[0]); // Print statement for first try
		for (int i = 1; i < guessDistribution.length; i++) {
			System.out.println((i + 1) + " guesses: " + guessDistribution[i]);
		}
		
	}
	
	/**
	 * This method reads from the file "statistics.txt" and gets the values for the user's statistics including the 
	 * number of games played, the number of wins, current streak, and max streak. It will update the statistics based 
	 * on if the user guessed the hidden word correctly or not this time. 
	 * It will also calculate the win rate and print all the values to the console.
	 * @param successful a boolean representing if the user guessed the the hidden word or not
	 * @throws FileNotFoundException
	 */
	public static void updateAndShowStats(boolean successful) throws FileNotFoundException {
		File targetFile = new File("statistics.txt");
		Scanner read = new Scanner(targetFile);
		int numOfGamesPlayed;
		int numOfWins;
		int currentStreak;
		int maxStreak;
		double winPercent;
		
		// Read values from file and assign them to the corresponding variables
		numOfGamesPlayed = read.nextInt();
		numOfWins = read.nextInt();
		currentStreak = read.nextInt();
		maxStreak = read.nextInt();
		read.close();
		
		// Accumulate games played and the number of wins if the player correctly guessed the word
		numOfGamesPlayed++;
		if (successful) {
			numOfWins++;
		}
		
		// Rewrite the current streak and check if there is a new max streak
		if (successful) {
			// Add one to streak if the user successfully guessed the word
			currentStreak++;
		} else {
			// If they couldn't guess it, reset the streak
			currentStreak = 0;
		}
		if (currentStreak > maxStreak) {
			// If current streak is greater than the max streak, rewrite the max streak
			maxStreak = currentStreak;
		}
		
		// Write the updated values back to file
		PrintWriter write = new PrintWriter(targetFile);
		write.println(numOfGamesPlayed);
		write.println(numOfWins);
		write.println(currentStreak);
		write.println(maxStreak);
		write.close();
		
		// Calculate win rate
		winPercent = (double) numOfWins / numOfGamesPlayed * 100;
		winPercent = Math.round(winPercent * 100) / 100.0; // round to two decimal places
		
		// Print statistics to console
		System.out.println("Statistics:");
		System.out.println("Games Played: " + numOfGamesPlayed);
		System.out.println("Win%: " + winPercent);
		System.out.println("Current Streak: " + currentStreak);
		System.out.println("Max Streak: " + maxStreak);
		
	}

}