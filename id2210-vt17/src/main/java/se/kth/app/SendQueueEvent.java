package se.kth.app;

import app.document.evaluator.Operation;
import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 5/22/2017.
 */
public class SendQueueEvent implements KompicsEvent {
    private final List<Operation> operations;
    public final KAddress sender;

    public SendQueueEvent(List<Operation> operations, KAddress sender) {
        this.operations = operations;
        this.sender = sender;
    }

    public List<Operation> getOperations() {
        List<Operation> copy = new ArrayList<>();
        for (Operation op : operations)
            copy.add(op);
        return copy;
    }
}
