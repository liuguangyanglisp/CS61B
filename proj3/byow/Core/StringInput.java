package byow.Core;

import byow.TileEngine.TETile;

import static byow.Core.Engine.*;
import static byow.Core.WorldGenerator.movePlayer;

public class StringInput implements InputSource {
    private String input;
    private int index;

    private TETile[][] tiles = null;
    private static TETile[][] saveTiles = null;

    private String gameCommand = "";

    public StringInput(String s) {
        index = 0;
        input = s;
    }

    public char getNextKey() {
        char key = Character.toUpperCase(input.charAt(index));
        if (tiles == null) {
            int length = gameCommand.length();
            if ((length == 0 && key == 'N')
                    || (length > 0 && Character.isDigit(key))
                    || (length > 1 && key == 'S')) {
                gameCommand += key;
                if (key == 'S') {
                    tiles = generateTiles(gameCommand);
                    saveTiles = tiles;
                }
            }
            if (length == 0 && key == 'L') {
                tiles = saveTiles;
            }
        }

        if (tiles != null) {
            if (key == 'S' || key == 'W' || key == 'A' || key == 'D') {
                gameCommand += key;
                tiles = movePlayer(tiles, key);
                saveTiles = tiles;
            }
        }

        index += 1;
        return key;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }

    public TETile[][] getTiles() {
        return tiles;
    }
}
