package se.kth.app.link;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class PL_Deliver implements KompicsEvent, Serializable, PatternExtractor<Class, KompicsEvent> {
    public final KAddress src;
    public final KompicsEvent payload;

    public PL_Deliver(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PL_Deliver{" +
                "src=" + src +
                ", payload=" + payload +
                '}';
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