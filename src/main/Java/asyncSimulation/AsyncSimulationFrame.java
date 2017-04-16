package asyncSimulation;

import org.dyn4j.dynamics.World;

import java.time.Duration;
import java.time.Instant;


public abstract class AsyncSimulationFrame {
    /**
     * Physic Engine
     */
    private World world;
    /**
     * The time stamp for the last iteration
     */
    private Instant last;

    private boolean isStopped;

    AsyncSimulationFrame() {
        this.world = new World();
        isStopped = false;
        initializeWorld();
    }

    protected abstract void initializeWorld();

    /**
     * Override this function to add control on simulation bodies
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
        Instant now = Instant.now();
        Duration duration = Duration.between(last, now);
        last = now;
        long eclapsedTime = duration.getSeconds();
        world.update(eclapsedTime);
    }

}
