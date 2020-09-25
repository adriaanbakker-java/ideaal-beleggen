package beleggingspakket.grafiekenscherm;

public class Point {
    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    private double X;
    private double Y;

    public Point(double aX, double aY) {
        X = aX;
        Y = aY;
    }
}
