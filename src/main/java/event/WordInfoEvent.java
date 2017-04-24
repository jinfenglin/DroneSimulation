package event;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;

/**
 * Currently assuming we only add object to the world
 */
public class WordInfoEvent {
    public Vector2 leftTop;
    public BodyFixture bodyFixture;

    public WordInfoEvent(BodyFixture bodyFixture, Vector2 leftTop) {
        this.bodyFixture = bodyFixture;
        this.leftTop = leftTop;
    }
}

