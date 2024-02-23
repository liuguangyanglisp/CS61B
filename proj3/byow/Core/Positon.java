package byow.Core;

public class Positon {
    private int x;
    private int y;

    Positon(int xNum, int yNum) {
        this.x = xNum;
        this.y = yNum;
    }

    /*This Position move x and y coordinate, and return new position.*/
    public Positon move(int xNum, int yNum) {
        Positon newPositon = new Positon(this.x + xNum, this.y + yNum);
        return newPositon;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
