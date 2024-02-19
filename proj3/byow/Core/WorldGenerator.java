package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;


public class WorldGenerator {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 50;

    private static final long SEED = 588;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        drawWorld(world);
        ter.renderFrame(world);
    }
    /*Draw a world. */
    public static void drawWorld(TETile[][]world) {
        //draw random room with random width and length
        drawManyRoom(world, 10 + RANDOM.nextInt(10));

        //draw hallway to connect rooms.
        drawHallway(world);

        //draw wall to surround room and hallway.
        drawWall(world);
    }


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
    /*Draw a tile. */
    public static void drawTile(TETile[][] world, Positon p, TETile tile) {
        world[p.x][p.y] = tile;
    }

    /*Draw a room.*/
    public static void drawRoom(TETile[][] world, Room room) {
        for (int x = 0; x < room.width; x++) {
            for (int y = 0; y < room.height; y++) {
                    drawTile(world, room.positon.move(x,y), Tileset.FLOOR);
                }
            }
        Room.roomList.addFirst(room);
    }

    /*Draw n room randomly. */
    public static void drawManyRoom(TETile[][] world, int n) {
        if (n <= 0) {
            return;
        }
        drawRoom(world, Room.generateRoom(world));
        drawManyRoom(world, n - 1);
    }


    /*Draw hallways to connect all the room. */
    public static void drawHallway(TETile[][]world) {
        for (int i = 0; i < Room.roomList.size() - 1; i++) {
            Positon a = Room.roomList.get(i).positon;
            Positon b = Room.roomList.get(i+1).positon;
            connectRoom(world, a, b);
        }
    }

    /*Draw tile from start Position to end Position to connect two rooms. */
    private static void connectRoom(TETile[][]world, Positon start, Positon end) {
        Positon p = start;
        while (p.x != end.x) {
            if (p.x < end.x) {
                p = p.move(1, 0);
            }
            if (p.x > end.x) {
                p = p.move(-1, 0);
            }
            drawTile(world,p, Tileset.FLOOR);
        }

        while (p.y != end.y) {
            if (p.y < end.y) {
                p = p.move(0, 1);
            }
            if (p.y > end.y) {
                p = p.move(0, -1);
            }
            drawTile(world,p, Tileset.FLOOR);
        }

    }

    /*Draw wall to surround rooms and hallways. */
    public static void drawWall(TETile[][] world) {
        for (int i = 0; i < WIDTH; i ++) {
            for (int j = 0; j < HEIGHT; j ++) {
               if (world[i][j].equals(Tileset.FLOOR)) {
                    buildWallAround(world, new Positon(i,j));
                }
            }
        }
    }

    /*draWall help function: build wall around a Position.*/
    private static void buildWallAround (TETile[][] world, Positon p) {
        for (int x = -1; x <=1; x++) {
            for (int y = -1; y<=1; y++) {
                Positon around = p.move(x, y);
                fillNothingTowall(world, around);
            }
        }
    }
    /*draWall help function: if position p is nothing, fill nothing to wall.*/
    private static boolean fillNothingTowall(TETile[][]world, Positon p) {
        if (p.x < 0 || p.x >WIDTH-1 || p.y < 0 || p.y > HEIGHT - 1) {
            return false;
        }
        if (world[p.x][p.y].equals(Tileset.NOTHING)) {
            world[p.x][p.y] = Tileset.WALL;
            return true;
        }
        return false;
    }
    /*Get width of world.*/
    public static int getWidth() {
        return WIDTH;
    }
    /*Get height of world.*/
    public static int getHeight() {
        return HEIGHT;
    }
    /*Get Random object. */
    public static Random getRandom() {
        return RANDOM;
    }

}
