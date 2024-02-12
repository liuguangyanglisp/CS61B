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

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.TREE;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WALL;
            case 4: return Tileset.MOUNTAIN;
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
    }

    public static void addHexagon(TETile[][] aWorld, Position p, int s) {
        TETile tile = randomTile();
        int rightmostTile = p.x + s + 2 * (s - 1);
        if (rightmostTile >= aWorld[0].length) {
            return;
        }
        drawSingleHexagon (aWorld, tile, p, s - 1, s, (2 * s));
    }

    private static void drawSingleHexagon (TETile[][] aWorld, TETile tileType,  Position p, int blank, int fill, int HexHeight) {
        if ((p.y - (HexHeight - 1)) < 0) {
            return;
        }

        drawRow(aWorld, tileType, p, blank, fill);
        if (blank > 0) {
            drawSingleHexagon (aWorld, tileType, new Position(p.x, p.y - 1), (blank - 1), (fill + 2), HexHeight - 2);
        }
        drawRow(aWorld, tileType, new Position(p.x, (p.y - (HexHeight - 1))), blank, fill);
    }

    private static void drawRow (TETile[][] aWorld, TETile tileType, Position p, int blank, int fill) {
        int worldWidth = aWorld[0].length;
        int px = p.x;
        int py = p.y;
        int maxPx = px + blank + fill - 1;
        if (px >= worldWidth || maxPx >= worldWidth) {
            return;
        }

        while (blank > 0) {
            px ++;
            blank --;
        }

        while (fill > 0) {
            aWorld[px][py] = tileType;
            px ++;
            fill --;
        }
    }

    public static boolean verticalTesselation (TETile[][] aWorld, Position p, int s) {
        int worldHeight = aWorld.length;
        if (p.y >= worldHeight || p.y < 0) {
            return false;
        }
        int spaceForNextHexagon = (p.y + 1) / (2 * s);
        if (spaceForNextHexagon < 1) {
            return false;
        }
        addHexagon(aWorld, p, s);
        if (spaceForNextHexagon > 1) {
            verticalTesselation(aWorld, new Position(p.x, p.y - (2 * s)), s);
        }
        return true;
    }

    public static void hexagonTesselation (TETile[][] aWorld, Position p, int s) {
        int worldWidth = aWorld[0].length;
        if (p.x >= worldWidth || p.x < 0) {
            return;
        }
        if (verticalTesselation(aWorld, p, s) == true) {
            int leftPositionX = p.x - (2 * s) + 1;
            int rightPositionX = p.x + (2 * s) - 1;
            int newPositionY = p.y - s;
            Position leftPosition = new Position(leftPositionX, newPositionY);
            Position rightPosition = new Position(rightPositionX, newPositionY);
            hexagonTesselation(aWorld, leftPosition, s);
            hexagonTesselation(aWorld, rightPosition, s);
        }
    }




    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // fill
        Position p = new Position(50,99);
        hexagonTesselation(world, p, 5);

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
