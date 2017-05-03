package se.kth.app.broadcast.CB;

import se.sics.kompics.KompicsEvent;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class CB_Deliver implements KompicsEvent {
    public final CBData m;

    public CB_Deliver(CBData m) {
        this.m = m;
    }
}
