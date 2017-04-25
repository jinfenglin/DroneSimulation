package asyncSimulation;

import com.google.gson.Gson;
import event.CollisionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;

/**
 * Created by jinfenglin on 4/25/17.
 */
public class CollisionDetector extends CollisionAdapter {
    private Body b1, b2;
    private static Logger logger;
    private Gson gson;

    public CollisionDetector(Body b1, Body b2) {
        gson = new Gson();
        logger = LogManager.getLogger("simLog");
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
        // the bodies can appear in either order
        if ((body1 == b1 && body2 == b2) ||
                (body1 == b2 && body2 == b1)) {
            String jsonString = gson.toJson(new CollisionEvent(body1.toString(), body2.toString()));
            logger.info("CollisionEvent:" + jsonString);
            return false;
        }
        return true;
    }
}
