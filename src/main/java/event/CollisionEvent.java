package event;

/**
 * Created by jinfenglin on 4/25/17.
 */
public class CollisionEvent {
    public String body1Info, body2Info;

    public CollisionEvent(String body1Info, String body2Info) {
        this.body1Info = body1Info;
        this.body2Info = body2Info;
    }
}
