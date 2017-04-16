package robotBody;


import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import java.util.Random;

public class Drone extends Body {
    /**
     * The predefined ideal intervals between drone's self checking
     */
    private double refreshCycle, deadline;
    /**
     * Delay to apply the next control algorithm
     */
    private double delay;
    /**
     * updateStatus the staus of the drone, determine how to apply force to itself
     */
    private int dealineMissCount;

    public void updateStatus(double eclapsedTime) {
        if (delay <= 0) {
            double latency = runBashCommand();
            delay = Math.max(refreshCycle, latency);
            if (delay > deadline) {
                dealineMissCount += 1;
            }
        }else{
            delay -= eclapsedTime;
        }
    }

    /**
     * Override this method of apply different algorithm running on drone, return the latency brought by this algorithm
     *
     * @return
     */
    protected double runBashCommand() {
        Random rand = new Random();
        this.applyForce(new Vector2(rand.nextDouble(), rand.nextDouble()));
        return 2;
    }

    public Drone(double refreshCycle, double deadline) {
        delay = 0;
        dealineMissCount = 0;
        this.deadline = deadline;
        this.refreshCycle = refreshCycle;
    }
}
