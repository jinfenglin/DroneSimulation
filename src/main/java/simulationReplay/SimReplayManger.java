package simulationReplay;

import asyncSimulation.ConfigureManger;
import event.SimulationDroneEvent;
import com.google.gson.Gson;
import event.WordInfoEvent;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import robotBody.Drone;
import utils.PhysicUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static utils.PhysicUtil.createObstacle;

public class SimReplayManger {
    List<SimulationDroneEvent> events;
    List<Body> obstacles;
    double cur_time;
    World world;
    Gson gson;

    private void initWorld() {
        world = new World();
        world.setGravity(new Vector2(0, 0));
        Drone testDrone = new Drone(0.5, 0.5);
        Vector2 d1Center = new Vector2(0, 0);
        double width = 0.45, height = 0.55;
        double mass = 3.99;
        BodyFixture b = testDrone.addFixture(Geometry.createRectangle(width, height));
        testDrone.setMass(MassType.NORMAL);
        testDrone.setMass(PhysicUtil.getMassForRectangle(d1Center, width, height, mass));
        testDrone.getFixture(0).setRestitution(0.8);
        testDrone.translate(ConfigureManger.getConfigureManger().getDroneStart());
        world.addBody(testDrone);
        for (Body body : obstacles) {
            world.addBody(body);
        }

    }

    public void readSimulationLog(String logPath) throws Exception {
        gson = new Gson();
        events = new LinkedList<>();
        obstacles = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader(logPath));
        String line = "";
        while ((line = bf.readLine()) != null) {
            int splitIndex = line.indexOf(':');
            String eventType = line.substring(0, splitIndex);
            String eventContent = line.substring(splitIndex + 1);
            if (eventType.equals("SimulationDroneEvent")) {
                SimulationDroneEvent event = gson.fromJson(eventContent, SimulationDroneEvent.class);
                events.add(event);
            } else if (eventType.equals("WorldInfoEvent")) {
                WordInfoEvent event = gson.fromJson(eventContent, WordInfoEvent.class);
                Body body = createObstacle(event.leftTop, event.rightBottom);
                obstacles.add(body);
            }
        }
    }


    public SimReplayManger(String logPath) throws Exception {
        readSimulationLog(logPath);
        initWorld();
        cur_time = 0;
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
            System.out.println(String.format("Event happends! EventTime=%s CurTime=%s, location=%s", event.time, cur_time, world.getBody(0).getWorldCenter()));
            events.remove(0);
            Drone drone = (Drone) world.getBody(0);
            drone.applyConstantForce(event.force);
            drone.setLinearVelocity(event.velocity);
        }
        //System.out.println(String.format("locatoin = %s", world.getBody(0).getWorldCenter()));
        world.update(elapsedTime);
        return false;
    }

    public List<Body> getBodies() {
        return world.getBodies();
    }
}
