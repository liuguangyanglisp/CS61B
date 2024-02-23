package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Room {
    Positon positon;
    int width;
    int height;

    public Room(Positon positon, int width, int height) {
        this.positon = positon;
        this.width = width;
        this.height = height;
    }

    /*Return ture if room is overlap.*/
    public static boolean overlap(TETile[][] world, Room room) {
        for (int x = room.positon.x; x < room.positon.x + room.width; x++) {
            for (int y = room.positon.y; y < room.positon.y + room.height; y++) {
                if (world[x][y] != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }
}
