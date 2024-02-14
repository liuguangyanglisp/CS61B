package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;


public class WorldGenerator {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 25;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void drawTile(TETile[][] world, Positon p, TETile tile) {
        world[p.x][p.y] = tile;
    }

    public static void drawRectangle(TETile[][] world, Positon p, TETile tile, int width, int length) {
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < length; y ++) {
                world[p.x + x][p.y + y] = tile;
            }
        }
    }
    public static void drawBuilding(TETile[][] world, Positon p, int width, int height) {
        if (width < 1 || height < 1) {
            return;
        }
        drawRectangle(world, p, Tileset.WALL, width + 2, height + 2);
        Positon floorStart = p.move(1, 1);
        drawRectangle(world, floorStart, Tileset.FLOOR, width, height);
    }

    public static void drawRoom (TETile[][] world) {
        Positon randomPosition = new Positon(RANDOM.nextInt(WIDTH - 1), RANDOM.nextInt(HEIGHT - 1));
        int randomWidhth = RANDOM.nextInt(WIDTH - randomPosition.x -1);
        int randomHight = RANDOM.nextInt(HEIGHT - randomPosition.y - 1);
        drawBuilding(world, randomPosition, randomWidhth, randomHight);
    }

    public static void drawWorld(TETile[][] world) {
        Positon randomPosition = new Positon(RANDOM.nextInt(WIDTH - 1), RANDOM.nextInt(HEIGHT - 1));
        int randomWidhth = RANDOM.nextInt(WIDTH - randomPosition.x -1);
        int randomHight = RANDOM.nextInt(HEIGHT - randomPosition.y - 1);

    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        Positon start = new Positon(0, 0);
        /*drawTile(world, start, Tileset.FLOOR);*/
        for (int x = 0; x < 1000; x ++) {
            drawRoom(world);
        }
        ter.renderFrame(world);
    }

    //The world must be a 2D grid, drawn using our tile engine. The tile engine is described in lab12.
    //The world must be pseudorandomly generated. Pseudorandomness is discussed in lab 12.
    //Rooms and hallways must have walls that are visually distinct from floors. Walls and floors should be visually distinct from unused spaces.
    //The world should be substantially different each time, i.e. you should not have the same basic layout with easily predictable features


    //The generated world must include distinct rooms and hallways, though it may also include outdoor spaces.
    //At least some rooms should be rectangular, though you may support other shapes as well.
    //Your world generator must be capable of generating hallways that include turns (or equivalently, straight hallways that intersect).
    //The world should contain a random number of rooms and hallways.
    //The locations of the rooms and hallways should be random.
    //The width and height of rooms should be random.

    //Hallways should have a width of 1 or 2 tiles and a random length.
    public static void drawHallway (TETile[][] world, Positon p, int length) {

    }
    //Rooms and hallways should be connected, i.e. there should not be gaps in the floor between adjacent rooms or hallways.
    //All rooms should be reachable, i.e. there should be no rooms with no way to enter

}
