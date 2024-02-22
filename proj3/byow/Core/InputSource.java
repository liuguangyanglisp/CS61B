package byow.Core;

import java.io.IOException;

public interface InputSource {
    public char getNextKey() throws IOException;
    public boolean possibleNextInput();
}
