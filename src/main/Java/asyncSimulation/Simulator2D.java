package asyncSimulation;


import org.apache.logging.log4j.LogManager;

public class Simulator2D extends AsyncSimulationFrame {

    @Override
    protected void initializeWorld() {
        LogManager.getLogger("simLog").info("Also a test");

    }

    public Simulator2D() {
    }

    public static void main(String[] args) {
        Simulator2D simulator2D = new Simulator2D();
    }

}

