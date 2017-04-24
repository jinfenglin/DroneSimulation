package utils;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

public class PhysicUtil {
    public static Mass getMassForRectangle(Vector2 center, double width, double height, double mass) {
        double interiaTensor = getInteriaTensor(width, height, mass);
        return new Mass(center, mass, interiaTensor);
    }

    public static double getInteriaTensor(double width, double height, double mass) {
        return mass / 12 * (width * width + height * height);
    }
}
