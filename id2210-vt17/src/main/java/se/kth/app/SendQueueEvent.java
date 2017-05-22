package se.kth.app;

import app.document.evaluator.Operation;
import se.sics.kompics.KompicsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 5/22/2017.
 */
public class SendQueueEvent implements KompicsEvent {
    private final List<Operation> operations;

    public SendQueueEvent(List<Operation> operations) {
        this.operations = operations;
    }

    public List<Operation> getOperations() {
        List<Operation> copy = new ArrayList<>();
        for (Operation op : operations)
            copy.add(op);
        return copy;
    }
}
