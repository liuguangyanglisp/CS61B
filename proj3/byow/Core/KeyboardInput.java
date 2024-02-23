package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import static byow.Core.Engine.*;
import static byow.Core.WorldGenerator.movePlayer;

public class KeyboardInput implements InputSource {
    /*private static final boolean PRINT_TYPED_KEYS = false;*/
    private String gameCommand = "";
    private int width = Engine.WIDTH * 10;
    private int height = width;
    private TERenderer ter = new TERenderer();

    private TETile[][] tiles = null;

    public KeyboardInput() {
        displayMenu();
    }

    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (tiles == null) {
                    int length = gameCommand.length();
                    if ((length == 0 && key == 'N')
                            || (length > 0 && Character.isDigit(key))
                            || (length > 1 && key == 'S')) {
                        gameCommand += key;
                        prompt("provide random seed number:" + gameCommand, 20);
                        if (key == 'S') {
                            tiles = generateTiles(gameCommand);
                            ter.initialize(WIDTH, HEIGHT);
                            ter.renderFrame(tiles);
                            saveGame("key.txt", gameCommand);
                        }
                    }

                    if (length == 0 && key == 'L') {
                        String gameCommandInfile = readGame("key.txt");
                        if (gameCommandInfile.length() > 3) {
                            gameCommand = gameCommandInfile;
                            tiles = generateTiles(gameCommandInfile);
                            ter.initialize(WIDTH, HEIGHT);
                            ter.renderFrame(tiles);
                            saveGame("key.txt", gameCommand);
                        }
                    }
                }

                if (tiles != null) {
                    if (key == 'S' || key == 'W' || key == 'A' || key == 'D') {
                        gameCommand += key;
                        saveGame("key.txt", gameCommand);
                        tiles = movePlayer(tiles, key);
                        ter.renderFrame(tiles);
                    }
                }
                return key;
            }
            if (tiles != null) {
                int x = (int) StdDraw.mouseX();
                int y = (int) StdDraw.mouseY();
                if (x < tiles.length && y < tiles[0].length) {
                    String message = tiles[x][y].description();
                    ter.renderFrameWithHUD(tiles, message);
                }
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }

    @Override
    public TETile[][] getTiles() {
        return tiles;
    }

    /*display a Main Menu that provides at LEAST the options:
    to start a new world,
    load a previously saved world,
    and quit. */
    private void displayMenu() {
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.black);
        Font font = new Font("Monaco", Font.BOLD, 30);
        Font titleFont = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(titleFont);
        StdDraw.text(width / 2, height / 2 + 3 * titleFont.getSize(), "CS61B: THE GAME");
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height / 2 + font.getSize(), "New Game (N)");
        StdDraw.text(width / 2, height / 2, "Load Game (L)");
        StdDraw.text(width / 2, height / 2 - font.getSize(), "Quit (Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /*show String in menu. */
    private void prompt(String s, int textSize) {
        StdDraw.clear(Color.black);
        Font font = new Font("Monaco", Font.BOLD, textSize);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
    }
}
