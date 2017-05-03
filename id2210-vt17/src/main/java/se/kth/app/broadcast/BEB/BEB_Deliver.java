package se.kth.app.broadcast.BEB;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class BEB_Deliver implements KompicsEvent {

    public final KAddress src;
    public final KompicsEvent payload;

    public BEB_Deliver(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }
}
