import java.util.ArrayList;

public class Shepards {
    private final double p;
    private final String method;
    private final double r;

    protected Shepards(double p, double r, String method) {
        this.p = p;
        this.r = r;
        this.method = method;
    }

    protected float run(Point point, ArrayList<Point> points) {
        double weightValueSum = 0;
        double weightNormSum = 0;

        // Get over list of known points, either all if BASIC method or nearest if MODIFIED method
        for (Point pointK : points) {
            // Calculate weight factor of pointK
            double weight = weightFactor(point, pointK);

            // Weight == -1 is returned when distance == 0, so we can return value yk of the point
            if (weight == -1) return (float) pointK.yk;

            weightValueSum += (weight * pointK.yk);
            weightNormSum += weight;
        }

        return (float) (weightValueSum / weightNormSum);
    }

    protected double weightFactor(Point point, Point p1) {
        // Calculate Euclidean distance
        float euclid_dis = point.distance(p1);

        // If distance equals 0 than return -1 so we can then set value of f to yk
        if (euclid_dis == 0) return -1;
        else if (method.equals("basic")) return 1 / Math.pow(euclid_dis, p);
        else return Math.pow(Math.max(0, r - euclid_dis) / (r * euclid_dis), 2);
    }
}
