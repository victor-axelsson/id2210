package se.kth.app.broadcast.GBEB;

import se.sics.kompics.KompicsEvent;

import java.util.List;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class GBEB_HistoryResponse implements KompicsEvent {

    private List<HistoryElement> history;

    public GBEB_HistoryResponse(List<HistoryElement> history) {
        this.history = history;
    }

    public List<HistoryElement> getHistory() {
        return history;
    }
}
