package se.kth.app.sim.behaviour.sim2;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;
import se.kth.app.sim.behaviour.BehaviourCreator;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public class PBehaviour implements Behaviour {


    @Override
    public void setup(Evaluator eval) {
        eval.receive(BehaviourCreator.EVAL_UNIQUE_ID, BehaviourCreator.getSimulation2Setup());

        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("colors")).evalExpr(new Expr.Get("red")).evalCmd(new Cmd.Assign(new Val.Str("#ff0000")));

        System.out.println(eval.toJsonString());
    }

}
