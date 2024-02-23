package byow.Core;
import byow.TileEngine.TETile;

public interface InputSource {
    char getNextKey();
    boolean possibleNextInput();
    TETile[][] getTiles();
}
