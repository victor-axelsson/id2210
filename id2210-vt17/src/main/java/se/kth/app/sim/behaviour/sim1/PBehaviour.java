package se.kth.app.sim.behaviour.sim1;

import app.document.evaluator.Evaluator;
import app.document.language.*;
import se.kth.app.sim.behaviour.Behaviour;
import se.kth.app.sim.behaviour.BehaviourCreator;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public class PBehaviour implements Behaviour {

    @Override
    public void setup(Evaluator eval) {

        eval.receive(BehaviourCreator.getSimulation1Setup());


        Cmd cmd2 = new Cmd.Assign(new Val.Str("B"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("key")).evalCmd(cmd2);
        System.out.println(eval.toJsonString());
    }

}
