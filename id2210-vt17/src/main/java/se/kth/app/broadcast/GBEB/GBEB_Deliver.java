package se.kth.app.broadcast.GBEB;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class GBEB_Deliver implements KompicsEvent, PatternExtractor<Class, KompicsEvent> {

    public final KAddress pp;
    public final KompicsEvent m;


    public GBEB_Deliver(KAddress pp, KompicsEvent m) {
        this.pp = pp;
        this.m = m;
    }


    @Override
    public Class extractPattern() {
        return m.getClass();
    }

    @Override
    public KompicsEvent extractValue() {
        return m;
    }
}
