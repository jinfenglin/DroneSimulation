package asyncSimulation;

import org.dyn4j.dynamics.Force;
import org.dyn4j.geometry.Vector2;

public class ConstantForce extends Force {
    private double validTime;

    public ConstantForce(Vector2 vec, double validTime) {
        super(vec);
        this.validTime = validTime;
    }

    public double getValidTime() {
        return validTime;
    }

    @Override
    public boolean isComplete(double elapsedTime) {
        if (validTime <= 0) {
            return true;
        } else {
            validTime -= elapsedTime;
            return false;
        }
    }
}
