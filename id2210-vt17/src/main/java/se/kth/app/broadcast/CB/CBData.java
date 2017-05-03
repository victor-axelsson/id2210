package se.kth.app.broadcast.CB;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.List;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class CBData implements KompicsEvent {
    private final List<CBData> past;
    private final KompicsEvent m;

    public CBData(List<CBData> past, KompicsEvent m) {
        this.past = past;
        this.m = m;
    }


    public List<CBData> getPast() {
        return past;
    }

    public KompicsEvent getM() {
        return m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CBData cbData = (CBData) o;

        //if (past != null ? !past.equals(cbData.past) : cbData.past != null) return false;
        return m != null ? m.equals(cbData.m) : cbData.m == null;
    }

    @Override
    public int hashCode() {
        return m.hashCode();
    }
}
