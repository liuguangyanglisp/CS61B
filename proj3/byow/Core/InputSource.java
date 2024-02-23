package byow.Core;
import byow.TileEngine.TETile;

public interface InputSource {
    public char getNextKey();

    public boolean possibleNextInput();

    public TETile[][] getTiles();
}
