package byow.Core;

import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {


    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        //getSeed from input.
        String seedString = getSeed(input);
        if (seedString == null) {
            return null;
        }
        long seed = Long.parseLong(seedString);

        //generate world from TETile[][] and seed.
        WorldGenerator world = new WorldGenerator(finalWorldFrame, seed);

        return world.getWorld();
    }

    private static String getSeed(String input) {
        StringInputDevice device = new StringInputDevice(input);
        String result = "";

        //get a sting like "N###S" format, which begins with N/n, has digit in the middle,
        //and end with S/s.
        while (device.possibleNextInput()) {
            char key = device.getNextKey();
            if (key == 'n' || key == 'N') {
                result = "N";
            } else if (result.length() >= 1 && Character.isDigit(key)) {
                result += key;
            } else if (result.length() >= 2 && (key == 's' || key == 'S')) {
                result += key;
                break;
            } else {
                result = "";
            }
        }

        //retrieve digit from the string.
        if (result.length() >= 3) {
            result = result.substring(1, result.length() - 1);
            return result;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        Engine x = new Engine();
        Engine y = new Engine();
        TETile[][]xt = x.interactWithInputString("n234s");
        TETile[][]yt = y.interactWithInputString("n234s");


        TERenderer ter = y.ter;
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(yt);
    }



}
