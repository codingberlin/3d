package terrains;

public class XY {
    int x;
    int y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void adjustToNewZero(final XY newZero) {
        this.x -= newZero.getX();
        this.y -= newZero.getY();
    }
}
