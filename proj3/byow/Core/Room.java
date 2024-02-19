package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.LinkedList;

import static byow.Core.WorldGenerator.*;

public class Room {
    Positon positon;
    int width;
    int height;
    static LinkedList<Room> roomList = new LinkedList<>();

    public Room(Positon positon, int width, int height) {
        this.positon = positon;
        this.width = width;
        this.height = height;
    }

    /*Generate random room in the world.
    * Random location, width, height. */
    public static Room generateRoom(TETile[][]world) {
        Room randomRoom;
        int x = 1 + getRandom().nextInt(getWidth() - 10);
        int y = 1 + getRandom().nextInt(getHeight() - 10);
        Positon randomPosition = new Positon(x, y);
        int randomWidth = 3 + getRandom().nextInt(5);
        int randomHeight = 3 + getRandom().nextInt(5);
        randomRoom = new Room(randomPosition, randomWidth, randomHeight);
        while (overlap(world, randomRoom)) {
            randomRoom = generateRoom(world);
        }
        return randomRoom;
    }

    /*Return ture if room is overlap.*/
    public static boolean overlap (TETile[][]world, Room room) {
        for (int x = room.positon.x; x < room.positon.x + room.width; x ++) {
            for (int y = room.positon.y; y< room.positon.y + room.height; y ++) {
                if (world[x][y] != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }
}
