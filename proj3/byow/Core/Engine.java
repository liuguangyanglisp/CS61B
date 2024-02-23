package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static byow.Core.WorldGenerator.movePlayer;


public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 40;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
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


    /*Return 2D TETile  from the input. */
    public static TETile[][] generateTiles(String input) {
        TETile[][] tiles = generateTilesFromNtoS(input);

        //Move TETile according move chars.
        int indexOfS = input.indexOf("S");
        if (indexOfS + 1 < input.length()) {
            String stringAfterS = input.substring(indexOfS + 1);
            tiles = moveTilesFromWSAD(tiles, stringAfterS);
        }
        return tiles;
    }

    /*Return 2D TETile from the number in the input(between N to S). */

    public static TETile[][] generateTilesFromNtoS(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        int indexOfN = -1;
        int indexOfS = -1;

        for (int i = 0; i < input.length(); i++) {
            char key = input.charAt(i);
            if (indexOfN == -1 && (key == 'N' || key == 'n')) {
                indexOfN = i;
            }
            if (indexOfS == -1 && (key == 'S' || key == 's')) {
                indexOfS = i;
            }
        }

        if (indexOfN == -1 || indexOfS == -1) {
            return null;
        }

        //build 2D TETile according number from N to S;
        TETile[][] world = new TETile[WIDTH][HEIGHT - 3];
        long seed = Long.parseLong(input.substring(indexOfN + 1, indexOfS));
        WorldGenerator worldGen = new WorldGenerator(world, seed);
        world = worldGen.getWorld();
        return world;
    }

    /*Move tiles according the moving input string, include W/S/A/D. */
    public static TETile[][] moveTilesFromWSAD(TETile[][] tiles, String input) {
        char lastSecondKey = 0;
        for (int n = 0; n < input.length(); n++) {
            char key = input.charAt(n);
            if (key == 'W' || key == 'S' || key == 'A' || key == 'D') {
                tiles = movePlayer(tiles, key);
            }
            if (lastSecondKey == ':' && key == 'Q') {
                return tiles;
            }
            lastSecondKey = key;
        }
        return tiles;

    }

    /*Save string into local file.*/
    public static void saveGame(String txtFileName, String gameCommand) {
        File txtFile = new File(txtFileName);
        if (!txtFile.isFile()) {
            return;
        }
        try {
            BufferedOutputStream str =
                    new BufferedOutputStream(java.nio.file.Files.newOutputStream(txtFile.toPath()));
            str.write(gameCommand.getBytes(StandardCharsets.UTF_8));
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /*Read string from local file.*/
    public static String readGame(String txtFileName) {
        File file = new File(txtFileName);
        if (!file.isFile()) {
            return "";
        }
        try {
            return new String(java.nio.file.Files.readAllBytes(file.toPath()));
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
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
        return inputSource.getTiles();
    }
}
