package se.kth.app.broadcast.CB;

import se.sics.kompics.KompicsEvent;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class CB_Broadcast implements KompicsEvent {

    public final KompicsEvent m;

    public CB_Broadcast(KompicsEvent m) {
        this.m = m;
    }
}
