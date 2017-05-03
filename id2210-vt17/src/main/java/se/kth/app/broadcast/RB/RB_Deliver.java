package se.kth.app.broadcast.RB;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class RB_Deliver implements KompicsEvent, PatternExtractor<Class, KompicsEvent> {

    public final KAddress src;
    public final KompicsEvent payload;

    public RB_Deliver(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

    @Override
    public Class extractPattern() {
        return payload.getClass();
    }

    @Override
    public KompicsEvent extractValue() {
        return payload;
    }
}
