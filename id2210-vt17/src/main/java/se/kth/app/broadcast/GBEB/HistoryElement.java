package se.kth.app.broadcast.GBEB;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class HistoryElement {

    private final KAddress p;
    private final KompicsEvent m;


    public HistoryElement(KAddress p, KompicsEvent m) {
        this.p = p;
        this.m = m;
    }

    public KAddress getP() {
        return p;
    }

    public KompicsEvent getM() {
        return m;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryElement that = (HistoryElement) o;

        if (p != null ? !p.equals(that.p) : that.p != null) return false;
        return m != null ? m.equals(that.m) : that.m == null;
    }

    @Override
    public int hashCode() {
        int result = p != null ? p.hashCode() : 0;
        result = 31 * result + (m != null ? m.hashCode() : 0);
        return result;
    }
}
