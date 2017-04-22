package simulationReplay;

import asyncSimulation.SimulationDroneEvent;
import com.google.gson.Gson;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import utils.PhysicUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class SimReplayManger {
    List<SimulationDroneEvent> events;
    double cur_time;
    World world;
    Gson gson;

    private void initWorld() {
        world = new World();
        world.setGravity(new Vector2(0, 0));
    }

    private void initEventList() {
        events = new LinkedList<>();
    }

    public void readSimulationLog(String logPath) throws Exception {
        gson = new Gson();
        initWorld();
        initEventList();
        BufferedReader bf = new BufferedReader(new FileReader(logPath));
        String line = "";
        while ((line = bf.readLine()) != null) {
            String[] logEntry = line.split(":");
            String eventType = logEntry[0];
            String eventContent = logEntry[1];
            if (eventType == "SimulationDroneEvent") {
                SimulationDroneEvent event = gson.fromJson(eventContent, SimulationDroneEvent.class);
                events.add(event);
            }
        }
    }


    public SimReplayManger(String logPath) throws Exception {
        readSimulationLog(logPath);
        cur_time = 0;
        /**
         * debug setup
         */
        Body testDrone = new Body();
        Vector2 d1Center = new Vector2(0, 0);
        double width = 0.45, height = 0.55;
        double mass = 3.99;
        testDrone.addFixture(Geometry.createRectangle(width, height));
        testDrone.setMass(PhysicUtil.getMassForRectangle(d1Center, width, height, mass));
        world.addBody(testDrone);
    }

    /**
     * Return true if the animation event list is empty else return false
     *
     * @param elapsedTime
     * @return
     */
    public boolean updateWorld(double elapsedTime) {
        if (events.size() == 0)
            return true;
        SimulationDroneEvent event = events.get(0);
        cur_time += elapsedTime;
        if (event.time <= cur_time) {
            events.remove(0);
            //Apply the event to the world

            event.velocity
        }
        world.update(elapsedTime);
        return false;
    }

    public List<Body> getBodies() {
        return world.getBodies();
    }
}
