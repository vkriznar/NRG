import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Main {

    private static void run(String method, double p, double r,
                            double min_x, double min_y, double min_z, double max_x, double max_y, double max_z,
                            int res_x, int res_y, int res_z) throws Exception {
        Octree octree = null;
        ArrayList<Point> data = storeInputFile();
        Shepards shepards = new Shepards(p, r, method);

        // If method is modified Shephard's algorithm then create octree and feed points into it
        if (method.equals("modified")) {
            Point bottomLeft = new Point(-5, -5, -5);
            Point topRight = new Point(5,5,5);
            octree = new Octree(bottomLeft, topRight, 10, 15);
            octree.fromData(data);
        }

        // Go over linear space from z to y to x and do interpolation
        for (double z : linspace(min_z, max_z, res_z)) {
            for (double y : linspace(min_y, max_y, res_y)) {
                for (double x : linspace(min_x, max_x, res_x)) {
                    Point point =  new Point(x, y, z);

                    // If method is MODIFIED Shephard's then data is points in range of radius r
                    if (method.equals("modified"))
                        data = octree.pointsInRange(point, r);

                    // Get float value and write it to output file in bytes
                    float value = shepards.run(point, data);
                    if (Float.isNaN(value)) value = 0;

                    System.out.write(ByteBuffer.allocate(4).putFloat(value).array());
                }
            }
        }
    }

    private static double[] linspace(double min, double max, int points) {
        // Create a linear space spanning from min to max with points as numbers of steps
        double[] d = new double[points];
        for (int i = 0; i < points; i++){
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    private static ArrayList<Point> storeInputFile() {
        BufferedReader reader;
        ArrayList<Point> points = new ArrayList();
        try {
            // Load file to BufferedReader
            reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();

            // Walk through file lines and split values 0-2 into (x ,y ,z) and 3. value to yk
            while (line != null) {
                String[] splitted = line.split(" ");
                Point p = new Point(Double.parseDouble(splitted[0]),
                        Double.parseDouble(splitted[1]),
                        Double.parseDouble(splitted[2]),
                        Double.parseDouble(splitted[3]));

                points.add(p);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }

    public static void main(String[] args) throws Exception {
        int i = 0;
        String method = null;
        double r = 0;
        double p = 0;
        double minX = 0;
        double minY = 0;
        double minZ = 0;
        double maxX = 0;
        double maxY = 0;
        double maxZ = 0;
        int resX = 0;
        int resY = 0;
        int resZ = 0;

        while(i < args.length) {
            if (args[i].equals("--method")) {
                method = args[i+1];
                i+=2;
            }
            else if (args[i].equals("--r")) {
                r = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--p")) {
                p = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--min-x")) {
                minX = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--min-y")) {
                minY = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--min-z")) {
                minZ = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--max-x")) {
                maxX = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--max-y")) {
                maxY = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--max-z")) {
                maxZ = Double.parseDouble(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--res-x")) {
                resX = Integer.parseInt(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--res-y")) {
                resY = Integer.parseInt(args[i+1]);
                i+=2;
            }
            else if (args[i].equals("--res-z")) {
                resZ = Integer.parseInt(args[i+1]);
                i+=2;
            }
        }

        run(method, p, r, minX, minY, minZ, maxX, maxY, maxZ, resX, resY, resZ);
    }
}
