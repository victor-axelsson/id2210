package se.kth.app.broadcast.GBEB;

import se.sics.kompics.KompicsEvent;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class GBEB_Broadcast implements KompicsEvent {

    public final KompicsEvent m;

    public GBEB_Broadcast(KompicsEvent m) {
        this.m = m;
    }
}
