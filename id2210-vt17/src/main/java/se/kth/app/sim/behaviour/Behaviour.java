package se.kth.app.sim.behaviour;

import app.document.evaluator.Evaluator;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;
import java.util.List;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public interface Behaviour extends Serializable {
    void onSample(List<KAddress> addrs);
    void setup(Evaluator eval);
}
