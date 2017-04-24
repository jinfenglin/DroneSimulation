package mapGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jinfenglin on 4/24/17.
 */
public class MapGenerator {
    private static Logger logger = LogManager.getLogger("graphLog");
    final int WIDTH = 1200;
    final int HEIGTH = 800;
    int number;
    int uppperBound = 20;
    Random randx, randy;

    List<Vector2> leftTopPoints, rightDownPoints;

    MapGenerator(int num) {
        leftTopPoints = new ArrayList<>();
        rightDownPoints = new ArrayList<>();
        this.number = num;
        randx = new Random(System.currentTimeMillis());
        randy = new Random(System.currentTimeMillis());
    }

    private void generateOnePolygen() {
        int xl = randx.nextInt(WIDTH);
        int yl = randy.nextInt(HEIGTH);
        int xrange = randx.nextInt(uppperBound)+1;
        int yrange = randy.nextInt(uppperBound)+1;
        int xr = xl + xrange;
        int yr = yl + yrange;
        boolean flag = true;
        if (xr < WIDTH && yr < HEIGTH) {
            Vector2 np1 = new Vector2(xl, yl);
            Vector2 np2 = new Vector2(xr, yr);
            for (int i = 0; i < leftTopPoints.size(); i++) {
                Vector2 p1 = leftTopPoints.get(i);
                Vector2 p2 = rightDownPoints.get(i);
                if (overlap(np1, np2, p1, p2)) {
                    flag = false;
                }
            }
            if (flag) {
                leftTopPoints.add(np1);
                rightDownPoints.add(np2);
            }
        }

    }

    public boolean overlap(Vector2 p1, Vector2 p2, Vector2 q1, Vector2 q2) {
        double xrange1 = p2.x - p1.x;
        double xrange2 = q2.x - q1.x;
        double yrange1 = p2.y - p1.y;
        double yrange2 = q2.y - q1.y;
        double max_x = Math.max(p2.x, q2.x);
        double min_x = Math.min(p1.x, q1.x);
        double max_y = Math.max(p2.y, q2.y);
        double min_y = Math.min(p1.y, q1.y);
        if (xrange1 + xrange2 > max_x - min_x && yrange1 + yrange2 > max_y - min_y)
            return true;
        return false;

    }

    public void generate() {
        while (leftTopPoints.size() < number) {
            generateOnePolygen();
        }
        for (int i = 0; i < number; i++) {
            Vector2 p1 = leftTopPoints.get(i);
            Vector2 p2 = rightDownPoints.get(i);
            logger.info(p1.x + "," + p1.y + "," + p2.x + "," + p2.y);
        }
    }

    public static void main(String[] args) {
        MapGenerator mg = new MapGenerator(20);
        mg.generate();
    }


}
