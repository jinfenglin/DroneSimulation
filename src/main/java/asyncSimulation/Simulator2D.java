package asyncSimulation;


import com.google.gson.Gson;
import event.SimulationDroneEvent;
import event.WordInfoEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import robotBody.Drone;
import utils.PhysicUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static utils.PhysicUtil.createObstacle;

public class Simulator2D extends AsyncSimulationFrame {
    private static Logger logger = LogManager.getLogger("simLog");
    private Map<UUID, Integer> drones;
    private List<Body> obstacles;
    private Gson gson;

    @Override
    protected void initializeWorld() {
        world.setGravity(new Vector2(0, 0));
        Drone d1 = new Drone(0.5, 0.5);
        Vector2 d1Center = new Vector2(0, 0);
        double width = 0.45, height = 0.55;
        double mass = 3.99;
        d1.addFixture(Geometry.createRectangle(width, height));
        d1.setMassType(MassType.NORMAL);
        d1.setMass(PhysicUtil.getMassForRectangle(d1Center, width, height, mass));
        d1.translate(ConfigureManger.getConfigureManger().getDroneStart());
        d1.getFixture(0).setRestitution(0.8);
        world.addBody(d1);
        for (Body obstacle : obstacles) {
            world.addBody(obstacle);
            world.addListener(new CollisionDetector(d1, obstacle));
        }
        drones.put(d1.getId(), 0);

    }

    @Override
    protected void update(double eclapsedTime) throws Exception {
        for (UUID id : drones.keySet()) {
            Drone drone = (Drone) world.getBody(drones.get(id));
            drone.sensorInput(world.getBodies());
            boolean statusChanged = drone.updateStatus(eclapsedTime);
            if (statusChanged) {
                String jsonString = gson.toJson(new SimulationDroneEvent(drone.getConstantForce(), drone.getLinearVelocity(), drone.getWorldCenter(), simulationTime));
                logger.info("SimulationDroneEvent:" + jsonString);
            }
        }
        super.update(eclapsedTime);
    }

    private void readGraph(String graphPath) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(graphPath));
            String line;
            while (((line = bf.readLine()) != null)) {
                String[] coordinate = line.split(",");
                Vector2 leftTop = new Vector2(Double.valueOf(coordinate[0]), Double.valueOf(coordinate[1]));
                Vector2 rightBottom = new Vector2(Double.valueOf(coordinate[2]), Double.valueOf(coordinate[3]));
                WordInfoEvent wEvent = new WordInfoEvent(leftTop, rightBottom);
                String jsonString = gson.toJson(wEvent);
                logger.info("WorldInfoEvent:" + jsonString);
                obstacles.add(createObstacle(leftTop, rightBottom));
            }
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    public Simulator2D() {
        super();
        gson = new Gson();
        drones = new HashMap<>();
        obstacles = new LinkedList<>();
        String graphPath = ConfigureManger.getConfigureManger().getGraphPath();
        readGraph(graphPath);
    }

    public static void main(String[] args) throws Exception {
        Simulator2D simulator2D = new Simulator2D();
        simulator2D.run();
    }

}

