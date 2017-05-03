package se.kth.app.broadcast.RB;

import se.sics.kompics.KompicsEvent;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class RB_Broadcast implements KompicsEvent {

    public final KompicsEvent message;

    public RB_Broadcast(KompicsEvent message) {
        this.message = message;
    }
}
