package asyncSimulation;

import org.dyn4j.geometry.Vector2;

/**
 * Created by jinfenglin on 4/25/17.
 */
public class ConfigureManger {
    private static ConfigureManger cfManger;
    private String graphPath, simLogPath;
    private Vector2 droneStart;

    protected ConfigureManger() {
        graphPath = "graph/graph-2017-04-25 21:03:24.log";
        simLogPath = "simulationLog/simLog-2017-04-26 15:32:33.log";
        droneStart = new Vector2(0, 20);

    }

    public static ConfigureManger getConfigureManger() {
        if (cfManger == null) {
            cfManger = new ConfigureManger();
        }
        return cfManger;
    }

    public String getGraphPath() {
        return graphPath;
    }

    public String getSimLogPath() {
        return simLogPath;
    }

    public Vector2 getDroneStart() {
        return droneStart;
    }
}
