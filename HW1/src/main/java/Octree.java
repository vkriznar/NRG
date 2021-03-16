import java.util.ArrayList;

public class Octree {
    private final int depthRemaining;
    private final int bucketSize;
    private Octree[] children = null;
    private ArrayList<Point> points = new ArrayList<Point>();
    private Point bottomLeft, topRight, midPoint;

    public Octree(Point bottomLeft, Point topRight, int depthRemaining, int bucketSize) throws Exception {
        if (!(bottomLeft.x < topRight.x || bottomLeft.y < topRight.y || bottomLeft.z < topRight.z)) {
            throw new Exception("The bounds are not properly set!");
        }

        this.depthRemaining = depthRemaining;
        this.bucketSize = bucketSize;
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    public void fromData(ArrayList<Point> data) throws Exception {
        for (Point p : data) {
            insert(p);
        }
    }

    public ArrayList<Point> pointsInRange(Point p, double r) {
        ArrayList<Point> resPoints = new ArrayList<Point>();

        if (!intersects(bottomLeft, topRight, p, r)) {
            return resPoints;
        }

        if (points != null) {
            for (Point point : points) {
                if (pointInSphere(point, p, r)) {
                    resPoints.add(point);
                }
            }
        }

        if (children != null) {
            for (Octree child : children) {
                resPoints.addAll(child.pointsInRange(p, r));
            }
        }

        return resPoints;
    }

    private void makeChildren() throws Exception {
        children = new Octree[8];
        double dx = (topRight.x - bottomLeft.x) / 2;
        double dy = (topRight.y - bottomLeft.y) / 2;
        double dz = (topRight.z - bottomLeft.z) / 2;
        midPoint = new Point(bottomLeft.x + dx, bottomLeft.y + dy, bottomLeft.z + dz);

        Octree childOctree = null;
        Point bottomLeftNew;
        Point topRightNew;
        for (int i = 0; i <= 7; i++) {
            switch (OctreeParts.get(i)) {
                case BottomLeftFront:
                    childOctree = new Octree(bottomLeft, midPoint, depthRemaining - 1, bucketSize);
                    break;
                case BottomRightFront:
                    bottomLeftNew = new Point(bottomLeft.x + dx, bottomLeft.y, bottomLeft.z);
                    topRightNew = new Point(midPoint.x + dx, midPoint.y, midPoint.z);
                    childOctree = new Octree(bottomLeftNew, topRightNew, depthRemaining - 1, bucketSize);
                    break;
                case BottomLeftBack:
                    bottomLeftNew = new Point(bottomLeft.x, bottomLeft.y + dy, bottomLeft.z);
                    topRightNew = new Point(midPoint.x, midPoint.y + dy, midPoint.z);
                    childOctree = new Octree(bottomLeftNew, topRightNew, depthRemaining - 1, bucketSize);
                    break;
                case BottomRightBack:
                    bottomLeftNew = new Point(bottomLeft.x + dx, bottomLeft.y + dy, bottomLeft.z);
                    topRightNew = new Point(midPoint.x + dx, midPoint.y + dy, midPoint.z);
                    childOctree = new Octree(bottomLeftNew, topRightNew, depthRemaining - 1, bucketSize);
                    break;
                case TopLeftFront:
                    bottomLeftNew = new Point(bottomLeft.x, bottomLeft.y, bottomLeft.z + dz);
                    topRightNew = new Point(midPoint.x, midPoint.y, midPoint.z + dz);
                    childOctree = new Octree(bottomLeftNew, topRightNew, depthRemaining - 1, bucketSize);
                    break;
                case TopRightFront:
                    bottomLeftNew = new Point(bottomLeft.x + dx, bottomLeft.y, bottomLeft.z + dz);
                    topRightNew = new Point(midPoint.x + dx, midPoint.y, midPoint.z + dz);
                    childOctree = new Octree(bottomLeftNew, topRightNew, depthRemaining - 1, bucketSize);
                    break;
                case TopLeftBack:
                    bottomLeftNew = new Point(bottomLeft.x, bottomLeft.y + dy, bottomLeft.z + dz);
                    topRightNew = new Point(midPoint.x, midPoint.y + dy, midPoint.z + dz);
                    childOctree = new Octree(bottomLeftNew, topRightNew, depthRemaining - 1, bucketSize);
                    break;
                case TopRightBack:
                    childOctree = new Octree(midPoint, topRight, depthRemaining - 1, bucketSize);
                    break;
            }

            children[i] = childOctree;
        }
    }

    private void insert(Point p) throws Exception {
        if (p.x < bottomLeft.x || p.x > topRight.x
                || p.y < bottomLeft.y || p.y > topRight.y
                || p.z < bottomLeft.z || p.z > topRight.z) {
            throw new Exception("Insertion point is out of bounds! X: " + p.x + " Y: " + p.y + " Z: " + p.z);
        }

        // If tree doesn't have children we're in leaf node, insert point
        if (children == null) {
            points.add(p);
            // If we have exceeded bucket size and we can still go deeper then we need to split tree into 8 subtrees
            if (points.size() > bucketSize && depthRemaining > 0) {
                makeChildren();
                for (Point point : points) {
                    insert(point);
                }
                points = null;
            }
        } else {
            int pos = determineNode(p);
            children[pos].insert(p);
        }
    }

    private int determineNode(Point p) {
        int pos;
        if (p.x <= midPoint.x) {
            if (p.y <= midPoint.y) {
                if (p.z <= midPoint.z)
                    pos = OctreeParts.BottomLeftFront.id;
                else
                    pos = OctreeParts.TopLeftFront.id;
            } else {
                if (p.z <= midPoint.z)
                    pos = OctreeParts.BottomLeftBack.id;
                else
                    pos = OctreeParts.TopLeftBack.id;
            }
        } else {
            if (p.y <= midPoint.y) {
                if (p.z <= midPoint.z)
                    pos = OctreeParts.BottomRightFront.id;
                else
                    pos = OctreeParts.TopRightFront.id;
            } else {
                if (p.z <= midPoint.z)
                    pos = OctreeParts.BottomRightBack.id;
                else
                    pos = OctreeParts.TopRightBack.id;
            }
        }

        return pos;
    }

    private boolean pointInSphere(Point p, Point center, double radius) {
        return p.distance(center) <= radius;
    }
    
    private boolean intersects(Point bottomLeft, Point topRight, Point sphereCenter, double radius) {
        float dmin = 0;
        if (sphereCenter.x < bottomLeft.x) {
            dmin += Math.pow(sphereCenter.x - bottomLeft.x, 2);
        } else if (sphereCenter.x > topRight.x) {
            dmin += Math.pow(sphereCenter.x - topRight.x, 2);
        }

        if (sphereCenter.y < bottomLeft.y) {
            dmin += Math.pow(sphereCenter.y - bottomLeft.y, 2);
        } else if (sphereCenter.y > topRight.y) {
            dmin += Math.pow(sphereCenter.y - topRight.y, 2);
        }

        if (sphereCenter.z < bottomLeft.z) {
            dmin += Math.pow(sphereCenter.z - bottomLeft.z, 2);
        } else if (sphereCenter.z > topRight.z) {
            dmin += Math.pow(sphereCenter.z - topRight.z, 2);
        }

        return dmin <= Math.pow(radius, 2);
    }
}
