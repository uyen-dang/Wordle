import java.util.*;
import java.io.*;
import java.lang.Math;
import java.util.Arrays;

public class Wordle {
 
    public static String secret; // variable for storing the secret, made static so any method could access it
    public static char[] secretArr; // char array for storing all characters of the secret
    public static String guess; // variable for storing the guess, its value changes everytime the user inputs a new guess
    public static char[] guessArr; //// char array for storing all characters of the guess
 
    // I choose to use the worddata.txt file 
    public static String[][] words = new String[8][]; // the jagged array for storing words of different lengths
    // the first array at index 0 (String[0]) is for storing words of length 3
    // the last array at index 8 (String[8]) is for storing words of length 10
    // other in-between arrays store words of lengths 4 to 9
    
    public static int WORD_LENGTH; // the specific word length the user chooses
    // also equal to secretArr.length() and guessArr.length() (the guess and the secret must have the same length)
    
    public static String victoryMess; // annoucing that the secret has been guessed correctly and the user has won
 
    public static Scanner keyboard = new Scanner(System.in); // for taking user inputs
 
    public static char[] messageArr = new char[WORD_LENGTH]; // for reporting the comparison of chars between secret and guess
 
    public static void intro() { // prints the game's introduction + instructions on how to play
        System.out.println("#########################################################");
        System.out.printf("# %-54s#%n", "Let\'s play Wordle.");
        System.out.printf("# %-54s#%n", "Your goal is to guess a secret word.");
        System.out.printf("# %-54s#%n", "The word may have duplicated letters.");
        System.out.printf("# %-54s#%n", "For each guess, you receive a feedback.");
        System.out.printf("# %-54s#%n", "\'H\' for hits, \'m\' for misss, and \'-\' for others.");
        System.out.printf("# %-54s#%n", "Your commands are as follows");
        System.out.printf("# %-54s#%n", " s for showing the secret,");
        System.out.printf("# %-54s#%n", " h for show the history, and");
        System.out.printf("# %-54s#%n", " g for giving up and terminating the present puzzle.");
        System.out.println("#########################################################");
    }
 
    // Read from file "worddata.txt" (said file to be placed outside of the folder containing this file).
    public static String[][] readAndDivide(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        sc.nextLine(); // to bypass the length of the shortest word
        sc.nextLine(); // to bypass the length of the longest word
        int total = 0; // for recording the total number of words for a specific word length
        int index = -1;
        int j = 0;
        while (sc.hasNextLine() && index < 8) {
            String nextScan = sc.nextLine();
            try {
                total = Integer.parseInt(nextScan); // if this block of code executes, the token is the total amount of words of a specific length
                index++;
                j = 0;
                // creating an array and dividing words of specific word lengths into smaller arrays of words
                words[index] = new String[total];
            } catch (NumberFormatException e) { // if this block of code executes, the token is the word
                words[index][j] = nextScan; // recording this word into the array of the according word length
                j++;
            }
        }
        sc.close();
        return words;
    }
 
    // Based on the user's choice of word length, generate a random integer that is the index of the array containing words of that specific word length.
    // Put all the chars of that secret into an array of chars for later uses.
    public static void createSecret(int index, String[][] words) {
        // randomly generating the index of the secret in the array of the chosen word length
        int iSecret = (int)(Math.random()*(words[index].length));
        // taking the secret from the array of that specific word length and assigning it to the variable secret
        secret = words[index][iSecret];
        // dividing all chars of the secret into an array for later uses
        secretArr = new char[secret.length()];
        for (int i = 0; i < secret.length(); i++) secretArr[i] = secret.charAt(i);
    }
    
    // Design Choice: for validating guesses, I choose (i2)
    public static boolean validateGuess(String guess) {
        boolean boolVal = false;
        // 1. Check if guess and secret have the same length. If not, prompt the user for another input.
        if (guess.length() != secret.length()) {
            System.out.println("The length of the guess does not match that of the secret. Try again.");
            return false;
        }
        // 2. Loop through every character in the guess while validating whether it also appears in the word data or not. 
        // If not, prompts the user for another input.
        else {
            // WORD_LENGTH - 3 is the index of an array contaning words of a specific length
            // example: words[0] is the array containing words of length 3 and words[7] is the array containing words of length 10
            for (int i = 0; i < words[WORD_LENGTH - 3].length; i++) {
                if (guess.equals(words[WORD_LENGTH - 3][i])) 
                    return true;
            }
            
            if (!boolVal)
                System.out.println("Word does not exist in the word data. Try again.");
        } 
        return boolVal;
    }
 
/*GAME MECHANIC: How the game works.
 
    The game mechanic mainly employs the use of:
        1. secretArr (char array): for storing characters of the secret.
        2. guessArr (char array): for storing characters of the guess.
        3. messageArr (char array): for storing characters of the message that will be converted to a String as an output to the user.

    Step 1: Put 'H' for hits.
        1. The program first loops through elements of the char arrays guessArr and secretArr to compare elements having the same index.
        2. If both elements having the same index are equal, then at that same index in messageArr, the program puts 'H' there for a hit.
        3. After the loop is completed, all positions of hits have been recorded in messageArr as having 'H'.
 
    Step 2: Put 'm' for misses.
        1. The program loops through elements of guessArr and secretArr (both arrays have the same length) to consider each letter.
        2. The boolean array alphabet is for recording whether a specific letter in the alphabet has been considered or not. If a letter 
        has appeared before in the array and has been considered, the letter will be skipped.
        3. Variables count, all, rights, wrongs:
            count = number of times a specific letter in secret appears in guess.
            all (including s_all and g_all) = number of times a specific letter appears in the array of char.
            rights = number of positions where hits happen.
            wrongs (including s_wrongs and g_wrongs) = number of positions where hits don't happen in an array of chars.
        4. The later part of the method will only happen for letter whose count > 0 (count = 0 means that specific letter in guess doesn't 
appear in secret at all ==> a hyphen to be put there).
        5. The two-dimensional int array indices[2][all] has the following structure:
            indices = [all indices in guess where a specific letter appears (the letter at that position must not be already contributing to a hit)]
            [elements of 0 and 1, where 1 means yes and 0 means no]
        6. Because (c3) requires that only one 'm' can be put for a letter in the secret not contributing to a hit
            => Math.random() generates a random index (randex) that indicates the random position to put 'm' for all cases of a specific 
letter.
            Each time a new random index is generated, it cannot be the same as the ones before it and therefore has to be a unique random 
index
            => do-while loops keep generating new random indices until a unique random index is encountered.
            indices[1][randex] = 1 means the element (an index of guess) at indices[0][randex] will be used to contribute to the one miss for all cases of the same letter.
        7. Example: secret = sender and guess = feeble. The case of the letter 'e':
                    indices = [2,5] ==> all indices of the letter 'e' in the guess
                              [0,1] ==> 0 means index 2 of guess is not chosen; 1 means index 5 of guess is chosen to put a miss in.
        8. After the position of 'm' has been considered, the program puts an element of 'm' in messageArr by the chosen index of guess.

    Step 3: Put '-' (hyphen) for neither.
        1. The program loops through elements of the char array messageArr. 
        2. When it encounters any empty element (' '), it will replace ' ' with '-'.
        3. After the loop is completed, every position where neither a hit nor a miss happened have been recorded in messageArr as having '-'.
   
    Step 4: gameMechanic() is where every step is assembled.
*/

    // Step 1
    public static void putHits() {
        for (int i = 0; i < WORD_LENGTH; i++) { // loop through elements of guessArr and secretArr
            if (guessArr[i] == secretArr[i]) { // anytime the same letter at the same position in both arrays, the program puts a 'H' at that index in messageArr
            messageArr[i] = 'H'; // this 'H' cannot be changed to other values
            }
        }
    }

    // Step 2
    public static void putMisses() {
        // for recording which letter has appeared in a secret/guess or not
        boolean[] alphabet = new boolean[26];
        Arrays.fill(alphabet, false);
        for (int i = 0; i < secretArr.length; i++) {
            if (alphabet[secretArr[i]-97] == false) {
                alphabet[secretArr[i]-97] = true;
            int count = 0;
            int s_all = 0; // the number of times a letter appears in secret
            int g_all = 0; // the number of times a letter appears in guess
            int rights = 0;
            int s_wrongs; // the number of times a letter in secret does not contribute to a hit
            int g_wrongs; // the number of times a letter in guess does not contribute to a hit
            for (int j = 0; j < guessArr.length; j++) {
                if (secretArr[i] == secretArr[j]) s_all++;
                if (guessArr[i] == guessArr[j]) g_all++;
                // counting the number of times a letter in guess also appears in secret
                if (secretArr[i] == guessArr[j]) count++;
            }
            if (guessArr[i] == secretArr[i]) rights++;
            // computing the number of wrongs
            s_wrongs = s_all - rights;
            g_wrongs = g_all - rights;
            // "count = 0" means this specific letter in secret doesn't appear in letter.
            if ((count > 0) && (s_wrongs > 0)) {
                // GENERATING A RANDOM INDEX (RANDEX)
                int[][] indices = new int[2][g_wrongs];
                // feeding guess's indices where a specific letter in secret occurs in guess into the array indices
                int z = 0;
                for (int y = 0; y < guessArr.length; y++) {
                    if ((messageArr[y] != 'H') && (secretArr[i] == guessArr[y])) {
                        for (int k = 0; k < indices[0].length; k++) {
                            if (indices[0][k] != y)
                                indices[0][z++] = y;
                        }
                    }
                }
                Arrays.fill(indices[1], 0);
                // randex: abbreviation for "random index"
                // loop through the total amount of g_wrongs and generate a randex to put m in
                for (int k = 0; k < g_wrongs; k++) {
                    boolean same = true;
                    // keep generating randices until an unique randex is generated
                    int randex = 0;
                    do {
                        randex = (int) (Math.random()*g_wrongs);
                        // making sure that the newly generated randex is unique in comparison with current randices
                        if (indices[1][randex] == 0) {
                            indices[1][randex] = 1;
                            same = false; // when the unique randex has been generated, this code will break out of the loop
                        }
                    } while (same);
                }
                // putting the misses into the array messageArr
                for (int x = 0; x < g_wrongs; x++) {
                    if ((indices[1][x] == 1) && (messageArr[indices[0][x]] != 'H')) 
                        messageArr[indices[0][x]] = 'm'; 
                }
            }
        }
    }
}
 
// Step 3
public static void putHyphens() { // anywhere neither a hit nor a miss happens, a '-' will be put there in messageArr
    for (int i = 0; i < WORD_LENGTH; i++) {
        if (messageArr[i] == ' ') messageArr[i] = '-';
    }
}
 
// GAME MECHANIC:
public static String gameMechanic(String guess, int input_length) {
    String message = " ";
    // Feeding all chars of a validated guess into an array of chars.
    if (input_length > 1) { // this block of code only runs when the input is not a single-word command
        guessArr = guess.toCharArray();
        // Putting the hits, misses, and hyphens.
        Arrays.fill(messageArr, ' ');
        putHits();
        putMisses();
        putHyphens();
        // Obtaining the message to be reported to the user.
        message = String.valueOf(messageArr);
    }
    return message;
    }
 
// Interacting with user input: Validating guesses and accepting single-word commands.
public static void interaction() {
    String input;
    int temp = 0;
    while (true) { // infinite loop => infinite number of attempts
        boolean boolVal = false;
        boolean stop = false;
        String guess = " ";
        String hist = "";
        do {
            // Accepting inputs from the user (both single-word commands and guesses).
            System.out.print("> ");
            input = keyboard.next().toLowerCase();

            // Single-word commands
            if (input.equals("s")) 
                System.out.println("Secret = " + secret); 
            else if (input.equals("h")) 
                System.out.print("History\n" + hist);
            else if (input.equals("g")) {
                System.out.print("History\n" + hist + "Secret = " + secret + "\n");
                stop = true;
            }
            // In the case that the input is a guess instead of a single-word command:
            else {
                guess = input;
                boolVal = validateGuess(guess); // Validate the guess. Will only execute the next step when the user input is valid (boolVal = 2)
                if (boolVal) {
                    System.out.println(gameMechanic(guess, input.length()));
                    // Updating the history to be printed to the screen should the user prompts for it.
                    hist += " " + (temp+1) + ":" + guess + ":" + gameMechanic(guess, input.length()) + "\n";
                    temp++;
                }
            }
        } while (!stop && (!boolVal || !(gameMechanic(guess, input.length()).equals(victoryMess))));
        
        if (stop == true) break; // break out of infinite loop when the input is 'g'
        
        // break out of infinite loop when the user has guessed correctly
        if (gameMechanic(guess, input.length()).equals(victoryMess)) {
            System.out.println("You've got it!");
            System.out.print("History\n" + hist);
            break;
        }
    }
}
 
// main method
public static void main(String[] args) throws FileNotFoundException{
    String ans = "n";
    do {
        intro();
        System.out.print("Choose the length for the secret: "); // Let the user choose the length for the secret.
        WORD_LENGTH = keyboard.nextInt();
        String[][] words = readAndDivide("worddata.txt"); // Read worddata.txt and divide words of specific word lengths into arrays accordingly.
        createSecret(WORD_LENGTH-3, words); // Create and store the chosen word in the variable secret.
        messageArr = new char[WORD_LENGTH];
        Arrays.fill(messageArr, ' '); // messageArr is empty at first.
        char[] victoryArr = new char[WORD_LENGTH]; 
        Arrays.fill(victoryArr, 'H');
        victoryMess = String.valueOf(victoryArr); // message for when the user has guessed correctly 
        
        interaction(); // Interacting with user input + game mechanic.
 
        // Ask for another game at the end of the program.
        System.out.print("Another game? (y/n) ");
        ans = keyboard.next();
        } while (ans.equals("y"));
        
        keyboard.close();
    } 
}