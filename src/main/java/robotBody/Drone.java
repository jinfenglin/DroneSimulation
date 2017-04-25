package robotBody;


import asyncSimulation.ConstantForce;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.PhysicUtil.findAdjacent;
import static utils.PhysicUtil.vec2str;

public class Drone extends Body {
    /**
     * The predefined ideal intervals between drone's self checking
     */
    private double refreshCycle, deadline;
    /**
     * Delay to apply the next control algorithm
     */
    private double delay;
    /**
     * updateStatus the staus of the drone, determine how to apply force to itself
     */
    private int dealineMissCount;
    private ConstantForce constantForce;
    private Map<String, Vector2> directionMap;
    private String gem5Dir = "/home/jinfenglin/Documents/gem5-gpu";
    private String gem5 = gem5Dir + "/gem5/build/VI_hammer/gem5.opt";
    private String config = gem5Dir + "/gem5-gpu/configs/se_fusion.py";
    private String benchmarkDir = gem5Dir + "/benchmarks/rodinia/droneControl";
    private String controlProgram = benchmarkDir + "/gem5_fusion_droneControl";
    private String inputData = benchmarkDir + "/input.txt";
    private String output_path = "m5out/stats.txt";

    double sensorRange;


    public Body applyConstantForce(ConstantForce force) {
        super.applyForce(force);
        constantForce = force;
        return this;
    }

    public void sensorInput(List<Body> bodies) throws Exception {
        List<Body> neighbour = findAdjacent(this, bodies, sensorRange);
        System.out.println(String.format("%s objects detected!", neighbour.size()));
        BufferedWriter bf = new BufferedWriter(new FileWriter(inputData));
        String dummyHeader1 = "2\n";
        String dummyHeader2 = "1\n";
        bf.write(dummyHeader1);
        bf.write(dummyHeader2);
        bf.write(vec2str(getWorldCenter()) + "\n");
        for (Body body : neighbour) {
            Rectangle re = (Rectangle) body.getFixture(0).getShape();
            double width = re.getWidth();
            double height = re.getHeight();
            Vector2 center = body.getWorldCenter();
            Vector2 leftTop = new Vector2(center.x - width / 2, center.y - height / 2);
            Vector2 rightBot = new Vector2(center.x + width / 2, center.y + height / 2);
            bf.write(vec2str(leftTop) + " " + vec2str(rightBot) + "\n");
        }
        bf.close();

    }


    public ConstantForce getConstantForce() {
        return constantForce;
    }

    /**
     * @param eclapsedTime
     * @return True if the status of drone the has been changed, False if the drone status not changed
     */
    public boolean updateStatus(double eclapsedTime) throws Exception {
        if (delay <= 0) {
            double latency = runBashCommand();
            delay = Math.max(refreshCycle, latency);
            if (delay > deadline) {
                dealineMissCount += 1;
            }
            return true;
        } else {
            delay -= eclapsedTime;
            return false;
        }
    }

    /**
     * Override this method of apply different algorithm running on drone, return the latency brought by this algorithm
     *
     * @return
     */
    protected double runBashCommand() throws InterruptedException, IOException {
        //Run the simulation

        String cmd = String.format("%s %s -c %s -o %s", gem5, config, controlProgram, inputData);
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();

        //Extract the direction and consumed time
        Pattern pattern = Pattern.compile("^direction\\d+$");
        BufferedReader bf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        String direction = "";
        while ((line = bf.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                direction = matcher.group(0);
                break;
            }
        }
        //Apply the force
        updateDirection(direction);
        return getDelay(output_path);
    }

    private double getDelay(String outputPath) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(outputPath));
        String line = "";
        double time = 0;
        while ((line = bf.readLine()) != null) {
            if (line.contains("sim_seconds")) {
                String[] parts = line.split(" ");
                List<String> merge = new ArrayList<>();
                for (String part : parts) {
                    if (part.length() > 0)
                        merge.add(part);
                }
                time = Double.valueOf(merge.get(1));
            }
        }
        bf.close();
        System.out.println("Delay=" + time);
        return time;
    }

    private void updateDirection(String direction) {
        Vector2 directionVec;
        try {
            directionVec = new Vector2(directionMap.get(direction));
        } catch (Exception e) {
            System.out.print("Direction not given, take default one!");
            directionVec = new Vector2(1, 0);
        }
        /**this.clearForce();
         this.clearAccumulatedForce();
         ConstantForce force = new ConstantForce(directionVec.multiply(10), refreshCycle );
         applyConstantForce(force);**/
        setLinearVelocity(directionVec.multiply(10));
    }

    private void initDirection() {
        directionMap = new HashMap<>();
        directionMap.put("direction1", new Vector2(0, -1));
        directionMap.put("direction2", new Vector2(0, 1));
        directionMap.put("direction3", new Vector2(-1, 0));
        directionMap.put("direction4", new Vector2(1, 0));
    }

    public Drone(double refreshCycle, double deadline) {
        delay = 0;
        dealineMissCount = 0;
        sensorRange = 5;
        initDirection();
        constantForce = new ConstantForce(new Vector2(0, 0), 0);
        this.deadline = deadline;
        this.refreshCycle = refreshCycle;
    }
}
