package se.kth.app.link;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class PL_Send implements KompicsEvent, Serializable {
    public final KAddress src;
    public final KAddress to;
    public final KompicsEvent payload;

    public PL_Send(KAddress src, KAddress to, KompicsEvent payload) {
        this.src = src;
        this.to = to;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PL_Send{" +
                "src=" + src +
                ", to=" + to +
                ", payload=" + payload +
                '}';
    }
}

