package byow.Core;

public class Positon {
    public static Positon drawPosition = new Positon(0,0);
    public int x;
    public int y;
    Positon(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /*This Position move x and y coordinate, and return new position.*/
    public Positon move(int x, int y) {
        Positon newPositon = new Positon(this.x + x, this.y + y);
        return newPositon;
    }
    /*Move a position x and y ordinate.*/
    public static void move (Positon p, int x, int y) {
        p.x = p.x + x;
        p.y = p.y + y;
    }
}
