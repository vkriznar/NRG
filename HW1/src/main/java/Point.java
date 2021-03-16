public class Point {
    public double x;
    public double y;
    public double z;
    public double yk;

    public Point(double x, double y, double z, double yk) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yk = yk;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Euclidean distance calculator
    public float distance(Point point) {
        double dis = Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2) + Math.pow(z - point.z, 2));
        return (float) dis;
    }

    public void print() {
        System.out.println(String.format("Point (%s, %s, %s) with value: %s",
                Double.toString(x), Double.toString(y), Double.toString(z), Double.toString(yk)));
    }
}
