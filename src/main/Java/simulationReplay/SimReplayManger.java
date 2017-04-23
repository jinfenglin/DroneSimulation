package simulationReplay;

import asyncSimulation.SimulationDroneEvent;
import com.google.gson.Gson;
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

        /**
         * debug setup
         */
        Drone testDrone = new Drone(0, 0);
        Vector2 d1Center = new Vector2(0, 2);
        double width = 0.45, height = 0.55;
        double mass = 3.99;
        BodyFixture b = testDrone.addFixture(Geometry.createRectangle(width, height));
        //b.setDensity(100);
        b.setRestitution(0.9);
        testDrone.setMass(MassType.NORMAL);
        testDrone.setMass(PhysicUtil.getMassForRectangle(d1Center, width, height, mass));
        testDrone.translate(2,2);
        world.addBody(testDrone);

        //test obstacle
        Body body = new Body();
        Vector2 obCenter = new Vector2(8, 4);
        double ob_width = 4, ob_height = 8;
        BodyFixture bf = body.addFixture(Geometry.createRectangle(ob_width, ob_height));;
        bf.setRestitution(0.9);
        body.setMass( MassType.NORMAL);
        body.translate(obCenter);

        world.addBody(body);
    }

    public void readSimulationLog(String logPath) throws Exception {
        gson = new Gson();
        events = new LinkedList<>();
        BufferedReader bf = new BufferedReader(new FileReader(logPath));
        String line = "";
        while ((line = bf.readLine()) != null) {
            int splitIndex = line.indexOf(':');
            String eventType = line.substring(0, splitIndex);
            String eventContent = line.substring(splitIndex + 1);
            if (eventType.equals("SimulationDroneEvent")) {
                SimulationDroneEvent event = gson.fromJson(eventContent, SimulationDroneEvent.class);
                events.add(event);
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
            System.out.print(String.format("Event happends! EventTime=%s CurTime=%s\n", event.time, cur_time));
            events.remove(0);
            //Apply the event to the world
            //TODO Add body id to log file, retrive object by id
            Drone drone = (Drone) world.getBody(0);
            drone.applyConstantForce(event.force);
        }
        world.update(elapsedTime);
        return false;
    }

    public List<Body> getBodies() {
        return world.getBodies();
    }
}
