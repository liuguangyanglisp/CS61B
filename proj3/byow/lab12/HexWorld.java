package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    private static final long SEED = 287313;
    private static final Random RANDOM = new Random(SEED);

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.FLOWER;
            case 1: return Tileset.MOUNTAIN;
            case 2: return Tileset.SAND;
            case 3: return Tileset.WATER;
            case 4: return Tileset.TREE;
            case 5: return Tileset.GRASS;
            default: return Tileset.AVATAR;
        }
    }

    private static class Position {
        public int x;
        public int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public Position shift (int x, int y) {
            Position p = new Position(this.x + x, this.y + y);
            return p;
        }
    }

    public static void addHexagon(TETile[][] world, Position p, int s) {
        if (s < 2) {
            return;
        }
        addHexagonHelp (world, randomTile(), p, s - 1, s);
    }

    /**
     * addHexagon help function, draw rows recursively, like in-order traversal.
     */
    private static void addHexagonHelp (TETile[][] world, TETile tile,  Position p, int b, int t) {
        //draw first row of Hexagon.
        Position firstRow = p.shift(b, 0);
        drawRow(world, tile, firstRow, t);

        //draw rows betweent first and end row.
        if (b > 0) {
            Position next = p.shift(0, -1);
            addHexagonHelp (world, tile, next, b-1, t+2);
        }

        //draw end row after all the rows have been drawn.
        Position endRow = firstRow.shift(0, -(2 * b + 1));
        drawRow(world, tile, endRow, t);
    }

    /**
     *Draw a row in world with tile from Positon P.
     */
    private static void drawRow (TETile[][] world, TETile tile, Position p, int length) {
        if (p.x + length > WIDTH) {
            return;
        }
        for (int n = 0; n < length; n ++) {
            world[p.x + n][p.y] = tile;
        }
    }

    /*
    *Draw n Hexagons(each one has side length of s) in a column.
    */
    public static void addHexagonColumn (TETile[][] world, Position p, int n, int s) {
        if (n == 0) {
            return;
        }
        addHexagon(world, p, s);
        Position next = p.shift(0, -2*s);
        addHexagonColumn(world, next, n-1, s);
    }

    /*HexagonTesselation from middle column(has m hexagons) to side column.
    *leftmost or rightmost side has n hexagons.
    * each hexagon has side length s. */
    public static void hexagonTesselation (TETile[][] world, Position p, int m, int n,  int s) {
        /**
         * if hexagonTesselation is beyond limits of world, don't run.
        int htHeight = n * 2 * s;
        int htLeftmost = p.x + (m - n) * (-(2 * s) + 1);
        int htRightmost = p.x + (m - n + 1) * (2 * s) - 1;
        if (p.y + 1 < htHeight || htLeftmost < 0 || htRightmost > WIDTH) {
            return;
        }*/
        addHexagonColumn(world, p, m, s);
        Position leftColumn = p;
        Position rightColumn = p;
        while (m > n) {
            m --;
            leftColumn = leftColumn.shift(-(2 * s) + 1, -s);
            rightColumn = rightColumn.shift((2 * s) - 1, -s);
            addHexagonColumn(world, leftColumn, m, s);
            addHexagonColumn(world, rightColumn, m, s);
        }

    }

    /**
     * Fills the given 2D array of tiles with Nothing.
     */
    private static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void drawWorld(TETile[][] world) {
        Position p = new Position(50, 99);
        hexagonTesselation(world, p, 6, 3, 3);
    }





    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        drawWorld(world);

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
