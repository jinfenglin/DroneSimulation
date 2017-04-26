package asyncSimulation;

import org.dyn4j.dynamics.World;

import java.util.concurrent.TimeUnit;


public abstract class AsyncSimulationFrame {
    /**
     * Physic Engine
     */
    protected World world;
    protected double simulationTime;
    protected double simulationCycle = 20/1000.0;

    private boolean isStopped;

    AsyncSimulationFrame() {
        this.world = new World();
        isStopped = false;
        simulationTime = 0;
    }

    protected abstract void initializeWorld();

    /**
     * Override this function to add control on simulation bodies
     *
     * @param elapsedTime
     */
    protected void update(double elapsedTime) throws Exception{
        world.update(elapsedTime);

    }

    public void stop() {
        this.isStopped = true;
    }

    public void run() throws Exception{
        this.initializeWorld();
        while (!isStopped && simulationTime < 35) {
            if (simulationTime % 2 < 0.02)
                System.out.println(simulationTime);
            loopIteration();
        }
    }

    private void loopIteration() throws Exception{
        double eclapsedTime = simulationCycle;
        simulationTime += eclapsedTime;
        update(eclapsedTime);
    }

}
