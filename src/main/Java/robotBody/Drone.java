package robotBody;


import asyncSimulation.ConstantForce;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import java.io.IOException;
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
    private ConstantForce constantForce;


    public Body applyConstantForce(ConstantForce force) {
        super.applyForce(force);
        constantForce = force;
        return this;
    }

    public ConstantForce getConstantForce() {
        return constantForce;
    }

    /**
     * @param eclapsedTime
     * @return True if the status of the has been changed, False if the drone status not changed
     */
    public boolean updateStatus(double eclapsedTime) throws Exception {
        if (delay <= 0) {
            double latency = runBashCommand();
            delay = Math.max(refreshCycle, latency);
            if (delay > deadline) {
                dealineMissCount += 1;
            }
            return true;
        } else {
            delay -= eclapsedTime;
            return false;
        }
    }

    /**
     * Override this method of apply different algorithm running on drone, return the latency brought by this algorithm
     *
     * @return
     */
    protected double runBashCommand() throws InterruptedException, IOException {
        Random rand = new Random();
        this.clearForce();
        this.clearAccumulatedForce();
        ConstantForce force = new ConstantForce(new Vector2(100, 0), 0.5);
        applyConstantForce(force);
        //setLinearVelocity(1, 1);

        String cmd = "cat /Users/jinfenglin/Downloads/hyper.txt";
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();

        //TODO Read delay from statistics

        return 1.65;
    }

    public Drone(double refreshCycle, double deadline) {
        delay = 0;
        dealineMissCount = 0;
        constantForce = new ConstantForce(new Vector2(0, 0), 0);
        this.deadline = deadline;
        this.refreshCycle = refreshCycle;
    }
}
