package byow.Core;

public class Positon {
    public int x;
    public int y;
    Positon(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Positon move(int x, int y) {
        Positon newPositon = new Positon(this.x + x, this.y + y);
        return newPositon;
    }
}
