package asyncSimulation;


import com.google.gson.Gson;
import event.SimulationDroneEvent;
import event.WordInfoEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import robotBody.Drone;
import utils.PhysicUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Simulator2D extends AsyncSimulationFrame {
    private static Logger logger = LogManager.getLogger("simLog");
    private Map<UUID, Integer> drones;
    private List<WordInfoEvent> obstacles;
    private Gson gson;

    @Override
    protected void initializeWorld() {
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
    protected void update(double eclapsedTime) throws Exception {
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

    private void readGraph(String graphPath) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(graphPath));
            String line;
            while (((line = bf.readLine()) != null)) {
                String[] coordinate = line.split(",");
                double width = Double.valueOf(coordinate[2]) - Double.valueOf(coordinate[0]);
                double height = Double.valueOf(coordinate[3]) - Double.valueOf(coordinate[1]);
                System.out.println(width + " " + height);
                Rectangle rectangle = new Rectangle(width, height);
                Vector2 leftTop = new Vector2(Double.valueOf(coordinate[0]), Double.valueOf(coordinate[1]));
                WordInfoEvent wEvent = new WordInfoEvent(new BodyFixture(rectangle), leftTop);
                String jsonString = gson.toJson(wEvent);
                logger.info("WorldInfoEvent:" + jsonString);
            }
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    public Simulator2D() {
        super();
        gson = new Gson();
        drones = new HashMap<>();
        obstacles = new ArrayList<>();
        readGraph("graph/graph-2017-04-24 18:27:12.log");
    }

    public static void main(String[] args) throws Exception {
        Simulator2D simulator2D = new Simulator2D();
        simulator2D.run();
    }

}

