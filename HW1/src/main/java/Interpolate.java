import java.io.*;
import java.util.ArrayList;

public class Interpolate {


    private static void run(String input_file, String output_file, String method, double p, double r,
                            double min_x, double min_y, double min_z, double max_x, double max_y, double max_z,
                            int res_x, int res_y, int res_z, boolean log, boolean save, boolean convert) throws Exception {
        Octree octree = null;
        ArrayList<Float> values = new ArrayList();
        ArrayList<Point> data = storeInputFile(input_file);
        Shepards shepards = new Shepards(p, r, method);

        // If method is modified Shephard's algorithm then create octree and feed points into it
        if (method == "modified") {
            Point bottomLeft = new Point(-5, -5, -5);
            Point topRight = new Point(5,5,5);
            octree = new Octree(bottomLeft, topRight, 10, 15);
            octree.fromData(data);
        }

        int counter = 1;

        // Go over linear space from z to y to x and do interpolation
        for (double z : linspace(min_z, max_z, res_z)) {
            System.out.println(String.format("Starting with step %d/%d", counter, res_z));
            counter++;
            for (double y : linspace(min_y, max_y, res_y)) {
                for (double x : linspace(min_x, max_x, res_x)) {
                    Point point =  new Point(x, y, z);

                    // If method is MODIFIED Shephard's then data is points in range of radius r
                    if (method.equals("modified"))
                        data = octree.pointsInRange(point, r);

                    // Get float value and store it into float ArrayList
                    float value = shepards.run(point, data);
                    if (Float.isNaN(value)) value = 0;
                    values.add(value);

                    if (log) {
                        System.out.println(String.format("Interpolated value at (%s, %s, %s): %s",
                                Double.toString(x),
                                Double.toString(y),
                                Double.toString(z),
                                Double.toString(value)));
                    }
                }
            }
        }

        if (save) saveToFile(output_file, method, values, convert);
    }

    private static double[] linspace(double min, double max, int points) {
        // Create a linear space spanning from min to max with points as numbers of steps
        double[] d = new double[points];
        for (int i = 0; i < points; i++){
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    private static ArrayList<Point> storeInputFile(String file) {
        BufferedReader reader;
        ArrayList<Point> points = new ArrayList();
        try {
            // Load file to BufferedReader
            reader = new BufferedReader(new FileReader("data/" + file + ".txt"));
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

    private static void saveToFile(String file, String method, ArrayList<Float> values, boolean convert) throws IOException {
        // Create new file stream
        FileOutputStream fileStream = new FileOutputStream("data/" + file + "_" + method + ".raw");

        // Input file stream into buffered output stream
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileStream);

        // Connect buffered stream to data output stream for writing primary data types to binary file
        DataOutputStream outputStream = new DataOutputStream(bufferedOutputStream);
        for (float value : values) {
            if (convert) {
                // Max value where most points reach ceiling
                float max = 2;
                // Normalise float values to uint-8 (0, 255)
                int intValue = (int) ((value / max) * 255);
                if (intValue > 255) intValue = 255;
                outputStream.writeInt(intValue);
            } else { outputStream.writeFloat(value); }
        }
        // Close output stream
        outputStream.flush();
        outputStream.close();
    }


    public static void main(String[] args) throws Exception {
        run("input1k", "output1k-t.raw", "modified", 0.5, 0.5,
                -1.5, -1.5, -1.5, 1.5, 1.5, 1.5, 16, 16, 16,
                true, true, false);
    }
}
