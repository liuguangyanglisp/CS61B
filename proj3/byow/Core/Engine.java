package byow.Core;

import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import com.google.common.io.ByteProcessor;
import com.google.common.io.Files;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static byow.Core.WorldGenerator.movePlayer;
import static byow.TileEngine.TERenderer.getTileSize;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 40;

    public static void main(String[] args) throws IOException {
        Engine e = new Engine();
        /*e.interactWithInputString("N999SDDDWWWDDD");*/
        /*e.interactWithInputString("N999SDDD:Q");
        e.interactWithInputString("LWWWDDD");*/

        /*e.interactWithInputString("N999SDDD:Q");
        e.interactWithInputString("LWWW:Q");
        e.interactWithInputString("LDDD:Q");*/

        e.interactWithInputString("N999SDDD:Q");
        e.interactWithInputString("L:Q");
        e.interactWithInputString("L:Q");
        e.interactWithInputString("LWWWDDD");


    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        char secondLastKey = 0;
        InputSource inputSource = new KeyboardInput();
        while (inputSource.possibleNextInput()) {
            char lastKey = inputSource.getNextKey();
            if (secondLastKey == ':' && lastKey == 'Q') {
                return;
            }
            secondLastKey = lastKey;
        }
    }


    /*Return a random 2D TETile according String begin with N.*/
    public static TETile[][] generateTiles(String input) {
        TETile[][] tiles = generateTilesFromNtoS(input);

        //Move TETile according move chars.
        int indexOfS = input.indexOf("S");
        String stringAfterS = input.substring(indexOfS);
        tiles = moveTilesFromWSAD(tiles, stringAfterS);

        return tiles;
    }

    public static TETile[][] generateTilesFromNtoS(String input) {
        int indexOfS = input.indexOf("S");
        //build 2D TETile according number from N to S;
        long seed = Long.parseLong(input.substring(1, indexOfS));
        TETile[][] world = new TETile[WIDTH][HEIGHT - 3];
        WorldGenerator worldGen = new WorldGenerator(world, seed);
        return worldGen.getWorld();
    }

    public static TETile[][] moveTilesFromWSAD(TETile[][] tiles, String input) {
        char lastSecondKey = 0;
        for (int n = 0; n < input.length(); n ++) {
            char key = input.charAt(n);
            if (key == 'W' || key =='S' || key == 'A' || key == 'D') {
                tiles = movePlayer(tiles, key);
            }
            if (lastSecondKey == ':' && key == 'Q') {
                return tiles;
            }
            lastSecondKey = key;
        }
        return tiles;

    }

    public static void saveGame(String txtFileName, String gameCommand) throws IOException {
        File txtFile = new File(txtFileName);
        Files.write(gameCommand, txtFile, StandardCharsets.UTF_8);
    }

    public static String readGame(String txtFileName) throws IOException {
        File txtFile = new File(txtFileName);
        if (!txtFile.exists()) {
            return "";
        }
        List<String> gameCommandList = Files.readLines(txtFile, StandardCharsets.UTF_8);
        String command = String.join("", gameCommandList);
        return command;
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
    public TETile[][] interactWithInputString(String input) throws IOException {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        char secondLastKey = 0;
        InputSource inputSource = new StringInput(input);
        while (inputSource.possibleNextInput()) {
            char lastKey = inputSource.getNextKey();
            if (secondLastKey == ':' && lastKey == 'Q') {
                break;
            }
            secondLastKey = lastKey;
        }
        String stringInFile = readGame("string.txt");
        return generateTiles(stringInFile);
        }

}
