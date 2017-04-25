package utils;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicUtil {
    public static Mass getMassForRectangle(Vector2 center, double width, double height, double mass) {
        double interiaTensor = getInteriaTensor(width, height, mass);
        return new Mass(center, mass, interiaTensor);
    }

    public static double getInteriaTensor(double width, double height, double mass) {
        return mass / 12 * (width * width + height * height);
    }

    public static Body createObstacle(Vector2 leftTop, Vector2 rightBottom) {
        Body body = new Body();
        Vector2 dif = rightBottom.difference(leftTop);
        body.addFixture(Geometry.createRectangle(dif.x, dif.y));
        body.setMassType(MassType.INFINITE);
        Vector2 moveVec = rightBottom.add(leftTop).multiply(0.5);
        body.translate(moveVec);
        return body;
    }

    public static String vec2str(Vector2 vec) {
        return vec.x + " " + vec.y;
    }

    public static List<Body> findAdjacent(Body core, List<Body> neighbours, double distanceThreshold) {
        List<Body> result = new ArrayList<>();
        for (Body body : neighbours) {
            if (body == core)
                continue;
            Vector2 center1 = body.getWorldCenter();
            Vector2 center2 = core.getWorldCenter();
            double distance = center1.distance(center2);
            if (distance < distanceThreshold) {
                result.add(body);
            }
        }
        return result;
    }
}
