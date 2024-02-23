package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Random;

import static byow.Core.Room.overlap;


public class WorldGenerator {
    private int WIDTH;
    private int HEIGHT;

    private Random RANDOM;

    private TETile[][] world;
    private LinkedList<Room> roomList = new LinkedList<>();

    public WorldGenerator(TETile[][] world, long seed) {
        this.world = world;
        this.WIDTH = world.length;
        this.HEIGHT = world[0].length;
        this.RANDOM = new Random(seed);
        drawWorld();
    }

    /*Draw a world. */
    public void drawWorld() {
        fillWithNothing();
        //draw random room with random width and length
        drawManyRoom(10 + RANDOM.nextInt(10));

        //draw hallway to connect rooms.
        drawHallway();

        //draw wall to surround room and hallway.
        drawWall();
        drawPlayer();
    }

    /*Draw a random avatar which can move by player. */
    private void drawPlayer() {
        while (true) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            if (world[x][y].equals(Tileset.FLOOR)) {
                Positon a = new Positon(x, y);
                drawTile(a, Tileset.AVATAR);
                return;
            }
        }
    }

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     */
    private void fillWithNothing() {
        for (int x = 0; x < this.WIDTH; x += 1) {
            for (int y = 0; y < this.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /*Draw a tile. */
    public void drawTile(Positon p, TETile tile) {
        world[p.x][p.y] = tile;
    }

    /*Draw a room.*/
    public void drawRoom(Room room) {
        for (int x = 0; x < room.width; x++) {
            for (int y = 0; y < room.height; y++) {
                drawTile(room.positon.move(x, y), Tileset.FLOOR);
            }
        }
        roomList.addFirst(room);
    }

    /*Draw n room randomly. */
    public void drawManyRoom(int n) {
        if (n <= 0) {
            return;
        }
        drawRoom(generateRoom());
        drawManyRoom(n - 1);
    }


    /*Draw hallways to connect all the room. */
    public void drawHallway() {
        for (int i = 0; i < roomList.size() - 1; i++) {
            Positon a = roomList.get(i).positon;
            Positon b = roomList.get(i + 1).positon;
            connectRoom(a, b);
        }
    }

    /*Draw tile from start Position to end Position to connect two rooms. */
    private void connectRoom(Positon start, Positon end) {
        Positon p = start;
        while (p.x != end.x) {
            if (p.x < end.x) {
                p = p.move(1, 0);
            }
            if (p.x > end.x) {
                p = p.move(-1, 0);
            }
            drawTile(p, Tileset.FLOOR);
        }

        while (p.y != end.y) {
            if (p.y < end.y) {
                p = p.move(0, 1);
            }
            if (p.y > end.y) {
                p = p.move(0, -1);
            }
            drawTile(p, Tileset.FLOOR);
        }

    }

    /*Draw wall to surround rooms and hallways. */
    public void drawWall() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j].equals(Tileset.FLOOR)) {
                    Positon p = new Positon(i, j);
                    buildWallAround(p);
                }
            }
        }
    }

    /*draWall help function: build wall around a Position.*/
    private void buildWallAround(Positon p) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Positon around = p.move(x, y);
                fillNothingTowall(around);
            }
        }
    }

    /*draWall help function: if position p is nothing, fill nothing to wall.*/
    private boolean fillNothingTowall(Positon p) {
        if (p.x < 0 || p.x >= WIDTH || p.y < 0 || p.y >= HEIGHT) {
            return false;
        }
        if (world[p.x][p.y].equals(Tileset.NOTHING)) {
            world[p.x][p.y] = Tileset.WALL;
            return true;
        }
        return false;
    }

    /*Generate random room in the world.
     * Random location, width, height. */
    public Room generateRoom() {
        Room randomRoom;
        int x = 1 + RANDOM.nextInt(WIDTH - 10);
        int y = 1 + RANDOM.nextInt(HEIGHT - 10);
        Positon randomPosition = new Positon(x, y);
        int randomWidth = 3 + RANDOM.nextInt(5);
        int randomHeight = 3 + RANDOM.nextInt(5);
        randomRoom = new Room(randomPosition, randomWidth, randomHeight);
        while (overlap(world, randomRoom)) {
            randomRoom = generateRoom();
        }
        return randomRoom;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public static TETile[][] movePlayer(TETile[][] world, char direction) {
        Positon player = new Positon(0, 0);

        //Find the player position. not an efficient algrithm.
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    player = new Positon(x, y);
                    break;
                }
            }
        }
        Positon moveTo = player;

        // move player.
        if (direction == 'W') {
            moveTo = player.move(0, 1);
        } else if (direction == 'S') {
            moveTo = player.move(0, -1);
        } else if (direction == 'A') {
            moveTo = player.move(-1, 0);
        } else if (direction == 'D') {
            moveTo = player.move(1, 0);
        }
        if (moveTo.x < 0 || moveTo.x >= world.length || moveTo.y < 0 || moveTo.y >= world.length) {
            return world;
        }
        if (world[moveTo.x][moveTo.y].equals(Tileset.FLOOR)) {
            world[moveTo.x][moveTo.y] = Tileset.AVATAR;
            world[player.x][player.y] = Tileset.FLOOR;
        }
        return world;
    }
}
