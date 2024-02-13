package byow.lab13;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Font;
import java.util.Objects;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(1000, 1000, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height squares as its canvas
         * Also sets up the scale so the bottom left is (0,0) and the top right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width, this.height);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear();
        StdDraw.enableDoubleBuffering();
        //TODO: Initialize random number generator
        rand = new Random(seed);
    }
    // Generate random string of letters of length n
    public String generateRandomString(int n) {
        String randomString = "";
        while (randomString.length() < n) {
            randomString = randomString + CHARACTERS[rand.nextInt(n)];
        }
        return randomString;
    }

    //Take the string and display it in the center of the screen
    //If game is not over, display relevant game information at the top of the screen
    public void drawFrame(String s) {

        StdDraw.clear();
        StdDraw.text(width/2, height/2, s);
        if (!gameOver) {
            String roundMessage = "Round: " + round;
            StdDraw.textLeft(0, this.height - 30, roundMessage);

            for (int x = 0; x < width; x ++) {
                StdDraw.textLeft(x, this.height - 40, "_");
            }

            if (playerTurn) {
                StdDraw.text(this.width/2, this.height - 30, "Type!");
            } else {
                StdDraw.textLeft(this.width/2, this.height - 30, "Watch!");
            }
            String encourgaeString = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
            StdDraw.textRight(this.width, this.height - 30, encourgaeString);
        }
        StdDraw.show();
    }

    /**
     * Display each character in letters, making sure to blank the screen between letters.
     */
    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i ++) {
            char letter = letters.charAt(i);
            drawFrame(Character.toString(letter));
            StdDraw.pause(1000);
            //Once all the letters have shown, change form "Watch!" to "Type!" at once, no delay.
            if (i + 1 >= letters.length()) {
                playerTurn = true;
            }
            drawFrame("");
            StdDraw.pause(500);
        }

    }

    //Read n letters of player input
    public String solicitNCharsInput(int n) {
        String input = "";
        while (input.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                input = input + StdDraw.nextKeyTyped();
                drawFrame(input);
            }
        }

        // try to optimize user experience, let user see all the input.
        StdDraw.pause(500);
        return input;
    }

    public void startGame() {
        //Set any relevant variables before the game starts
        gameOver = false;
        round = 1;

        //Establish Engine loop
        while (!gameOver) {
            playerTurn = false;
            drawFrame("Rount: " + round);
            StdDraw.pause(1000);
            String randomString = generateRandomString(round);
            flashSequence(randomString);

            String playerInput = solicitNCharsInput(round);
            if (Objects.equals(randomString, playerInput)) {
                round ++;
            } else {
                gameOver = true;
            }
        }
        String ggMessage = "Game Over! You made it to round:" + round;
        drawFrame(ggMessage);
    }

}
