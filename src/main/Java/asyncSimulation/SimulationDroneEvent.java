package asyncSimulation;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

public class SimulationDroneEvent {
    /**
     * public Vector2 wordCenter;
     * public Vector2 velocity;
     * public ConstantForce force;
     */
    public double time;
    World world;

    public SimulationDroneEvent(ConstantForce force, Vector2 velocity, Vector2 wordCenter, double time) {
        /**this.wordCenter = wordCenter;
         this.force = force;
         this.velocity = velocity;**/
        this.time = time;
    }

    public SimulationDroneEvent(World world, double time) {
        this.world = world;
        this.time = time;
    }
}
