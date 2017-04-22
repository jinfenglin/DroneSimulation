package asyncSimulation;


import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import robotBody.Drone;
import utils.PhysicUtil;

import java.util.*;

public class Simulator2D extends AsyncSimulationFrame {
    private static Logger logger = LogManager.getLogger("simLog");
    private Map<UUID, Integer> drones;
    private Gson gson;

    @Override
    protected void initializeWorld() {
        drones = new HashMap<>();
        world.setGravity(new Vector2(0, 0));
        Drone d1 = new Drone(0.5, 0.5);
        Vector2 d1Center = new Vector2(0, 0);
        double width = 0.45, height = 0.55;
        double mass = 3.99;
        d1.addFixture(Geometry.createRectangle(width, height));
        d1.setMass(PhysicUtil.getMassForRectangle(d1Center, width, height, mass));
        world.addBody(d1);
        drones.put(d1.getId(), 0);
    }

    @Override
    protected void update(double eclapsedTime) {
        for (UUID id : drones.keySet()) {
            Drone drone = (Drone) world.getBody(drones.get(id));
            boolean statusChanged = drone.updateStatus(eclapsedTime);
            if (statusChanged) {
                String jsonString = gson.toJson(new SimulationDroneEvent(drone.getConstantForce(), drone.getLinearVelocity(), drone.getWorldCenter(), simulationTime));
                logger.info("SimulationDroneEvent:" + jsonString);
            }

        }
        super.update(eclapsedTime);
    }

    public Simulator2D() {
        super();
        gson = new Gson();
    }

    public static void main(String[] args) {
        Simulator2D simulator2D = new Simulator2D();
        simulator2D.run();
    }

}

