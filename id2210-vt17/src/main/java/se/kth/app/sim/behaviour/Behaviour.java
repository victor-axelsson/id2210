package se.kth.app.sim.behaviour;

import app.document.evaluator.Evaluator;

import java.io.Serializable;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public interface Behaviour extends Serializable {
    void actOnIt(Evaluator eval);
    void setup(Evaluator eval);
}
