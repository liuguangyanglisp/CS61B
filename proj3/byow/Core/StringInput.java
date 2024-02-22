package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.IOException;

import static byow.Core.Engine.*;
import static byow.Core.WorldGenerator.movePlayer;

public class StringInput implements InputSource{
    private String input;
    private int index;
    private TERenderer ter = new TERenderer();

    private TETile[][] tiles;

    private String gameCommand = "";

    public StringInput(String s) {
        index = 0;
        input = s;
    }

    public char getNextKey() throws IOException {
        char key = input.charAt(index);
        if (tiles == null) {
            int length = gameCommand.length();
            if ((length == 0 && key == 'N')
                    || (length > 0 && Character.isDigit(key))
                    || (length > 1 && key == 'S')) {
                gameCommand += key;
                if (key == 'S') {
                    tiles = generateTiles(gameCommand);
                    saveGame("string.txt", gameCommand);
                }
            }
            if (length == 0 && key == 'L') {
                String gameCommandInfile = readGame("string.txt");
                if (gameCommandInfile.length() > 3) {
                    gameCommand = gameCommandInfile;
                    tiles = generateTiles(gameCommandInfile);
                    saveGame("string.txt", gameCommand);
                }
            }
        }

        if (tiles != null) {
            if (key == 'S' || key == 'W' || key == 'A' || key == 'D') {
                gameCommand += key;
                tiles = movePlayer(tiles, key);
                saveGame("string.txt", gameCommand);
            }
        }

        index += 1;
        return key;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
