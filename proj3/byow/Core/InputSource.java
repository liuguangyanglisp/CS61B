package byow.Core;

import byow.TileEngine.TETile;

import java.io.IOException;

public interface InputSource {
    public char getNextKey();
    public boolean possibleNextInput();
    public TETile[][] getTiles();
}
