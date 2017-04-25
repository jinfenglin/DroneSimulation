package event;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;

/**
 * Currently assuming we only add object to the world
 */
public class WordInfoEvent {
    public Vector2 leftTop;
    public Vector2 rightBottom;

    public WordInfoEvent(Vector2 leftTop, Vector2 rightBottom) {
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }
}

