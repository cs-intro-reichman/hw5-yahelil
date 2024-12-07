/*
 * RUNI version of the Scrabble game.
 */

import java.util.Scanner;

public class Scrabble {
	// Note 1: "Class variables", like the five class-level variables declared below,
	// are global variables that can be accessed by any function in the class. It is
	// customary to name class variables using capital letters and underline characters.
	// Note 2: If a variable is declared "final", it is treated as a constant value
	// which is initialized once and cannot be changed later.

	// Dictionary file for this Scrabble game
	static final String WORDS_FILE = "dictionary.txt";

	// The "Scrabble value" of each letter in the English alphabet.
	// 'a' is worth 1 point, 'b' is worth 3 points, ..., z is worth 10 points.
	static final int[] SCRABBLE_LETTER_VALUES = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
												  1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10 };

	// Number of random letters dealt at each round of this Scrabble game
	static int HAND_SIZE = 10;

	// Maximum number of possible words in this Scrabble game
	static int MAX_NUMBER_OF_WORDS = 100000;

    // The dictionary array (will contain the words from the dictionary file)
	static String[] DICTIONARY = new String[MAX_NUMBER_OF_WORDS];

	// Actual number of words in the dictionary (set by the init function, below)
	static int NUM_OF_WORDS;

	// Populates the DICTIONARY array with the lowercase version of all the words read
	// from the WORDS_FILE, and sets NUM_OF_WORDS to the number of words read from the file.
	public static void init() {
		In in = new In(WORDS_FILE);
        System.out.println("Loading word list from file...");
        NUM_OF_WORDS = 0;
		while (!in.isEmpty()) {
			// Reads the next "token" from the file. A token is defined as a string of 
			// non-whitespace characters. Whitespace is either space characters, or  
			// end-of-line characters.
			DICTIONARY[NUM_OF_WORDS++] = in.readString().toLowerCase();
		}
        System.out.println(NUM_OF_WORDS + " words loaded.");
	}

	// Checks if the given word is in the dictionary.
	public static boolean isWordInDictionary(String word) {
		for (int i = 0; i < NUM_OF_WORDS; i++) {
			if (DICTIONARY[i].equals(word)) {
				return true; // Word found in the dictionary
			}
		}
		return false;
	}
	
	// Returns the Scrabble score of the given word.
	// If adds 50 points to the score.
	// If the word includes the sequence "runi", adds 1000 points to the game.the length of the word equals the length of the hand, 
	public static int wordScore(String word) {
		int score = 0;
		for(int i = 0; i<word.length(); i++){
			score += SCRABBLE_LETTER_VALUES[word.charAt(i) - 'a'];
		}
		score = score * word.length();
		if (word.length() == HAND_SIZE){
			score += 50;
		}
		if (isSubsetOfHand("runi", word)){
			score += 1000;
		}
		
		return score;
	}

	// Creates a random hand of length (HAND_SIZE - 2) and then inserts
	// into it, at random indexes, the letters 'a' and 'e'
	// (these two vowels make it easier for the user to construct words)
	public static String createHand() {
		StringBuilder hand = new StringBuilder();

		// Generate HAND_SIZE - 2 random letters
		for (int i = 0; i < HAND_SIZE - 2; i++) {
			char randomLetter = (char) ('a' + (int) (Math.random() * 26)); // Random letter from 'a' to 'z'
			hand.append(randomLetter);
		}
	
		// Add 'a' and 'e' at random positions
		hand.insert((int) (Math.random() * hand.length()), 'a'); // Insert 'a'
		hand.insert((int) (Math.random() * hand.length()), 'e'); // Insert 'e'
	
		// Return the hand as a string
		return hand.toString();
	}
	
    // Runs a single hand in a Scrabble game. Each time the user enters a valid word:
    // 1. The letters in the word are removed from the hand, which becomes smaller.
    // 2. The user gets the Scrabble points of the entered word.
    // 3. The user is prompted to enter another word, or '.' to end the hand. 
    public static void playHand(String hand) {
        int score = 0;
        StringBuilder handBuilder = new StringBuilder(hand);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Current Hand: " + spacedString(handBuilder.toString()));

        while (handBuilder.length() > 0) {
            System.out.print("Enter a word, or '.' to finish playing this hand: ");
            String input = scanner.nextLine();

            if (".".equals(input)) {
                break;
            }

            if (isWordInDictionary(input) && isSubsetOfHand(input, handBuilder.toString())) {
                for (char c : input.toCharArray()) {
                    int index = handBuilder.indexOf(String.valueOf(c));
                    if (index != -1) {
                        handBuilder.deleteCharAt(index);
                    }
                }

                score += wordScore(input);
                System.out.println("Current Hand: " + spacedString(handBuilder.toString()));
                System.out.println("Word score: " + wordScore(input));
            } else {
                System.out.println("Invalid word. Please try again.");
            }
        }

        System.out.println("End of hand. Total score: " + score + " points");
    }
	

	// Checks if the input word can be formed using the letters in the hand
	public static boolean isSubsetOfHand(String word, String hand) {
		StringBuilder handBuilder = new StringBuilder(hand);
		for (char c : word.toCharArray()) {
			int index = handBuilder.indexOf(String.valueOf(c)); // Find the index of the character in hand
			if (index == -1) {
				return false; // Character not found in the hand
			}
			handBuilder.deleteCharAt(index); // Remove the used character from the hand
		}
		return true; // All characters in word are in hand
	}
	public static String spacedString(String s) {
        return s.replaceAll("", " ").trim();
    }
	// Plays a Scrabble game. Prompts the user to enter 'n' for playing a new hand, or 'e'
	// to end the game. If the user enters any other input, writes an error message.
	public static void playGame() {
		// Initializes the dictionary
    	init();
		// The variable in is set to represent the stream of characters 
		// coming from the keyboard. Used for getting the user's inputs.  
		In in = new In();

		while(true) {
			System.out.println("Enter n to deal a new hand, or e to end the game:");
			String input = in.readString();
			while (!input.equals("e") && !input.equals("n")){
				System.out.println("Enter either n or e please");
				input = in.readString();
			}
			if (input.equals("e")){
				break;
			}
			playHand(createHand());
		}
	}

	public static void main(String[] args) { 
		playGame();
	}
	
	public static void testBuildingTheDictionary() {
		init();
		// Prints a few words
		for (int i = 0; i < 5; i++) {
			System.out.println(DICTIONARY[i]);
		}
		System.out.println(isWordInDictionary("mango"));
	}
	
	public static void testScrabbleScore() {
		System.out.println(wordScore("cat"));	
		System.out.println(wordScore("dog"));
		System.out.println(wordScore("quiz"));
		System.out.println(wordScore("friendship"));
		System.out.println(wordScore("running"));
	}
	
	public static void testCreateHands() {
		System.out.println(createHand());
		System.out.println(createHand());
		System.out.println(createHand());
	}
	public static void testPlayHands() {
		init();
		//playHand("ocostrza");
		//playHand("arbffip");
		playHand("aretiin");
	}
}
