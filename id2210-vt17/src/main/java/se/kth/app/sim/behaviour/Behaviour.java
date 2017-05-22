package se.kth.app.sim.behaviour;

import app.document.evaluator.Evaluator;
import app.document.evaluator.Operation;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;
import java.util.List;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public interface Behaviour extends Serializable {
    void setup(Evaluator eval);
}
