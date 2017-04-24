package event;

import org.dyn4j.dynamics.Body;

/**
 * Currently assuming we only add object to the world
 */
public class WordInfoEvent {
    public Body body;

    public WordInfoEvent(Body body) {
        this.body = body;
    }
}

