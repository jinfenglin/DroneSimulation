package asyncSimulation;

import org.dyn4j.dynamics.World;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


public abstract class AsyncSimulationFrame {
    /**
     * Physic Engine
     */
    protected World world;
    /**
     * The time stamp for the last iteration
     */
    private Instant last;
    protected double simulationTime;
    protected double simulationCycle = TimeUnit.MILLISECONDS.toSeconds(5);

    private boolean isStopped;

    AsyncSimulationFrame() {
        this.world = new World();
        isStopped = false;
        simulationTime = 0;
        initializeWorld();
    }

    protected abstract void initializeWorld();

    /**
     * Override this function to add control on simulation bodies
     *
     * @param elapsedTime
     */
    protected void update(double elapsedTime) {
        world.update(elapsedTime);

    }

    public void stop() {
        this.isStopped = true;
    }

    public void run() {
        this.last = Instant.now();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!isStopped) {
                    loopIteration();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.run();

    }

    private void loopIteration() {
        double eclapsedTime = simulationCycle;
        simulationTime += simulationCycle;
        world.update(eclapsedTime);
    }

}
