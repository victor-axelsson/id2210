package se.kth.app.sim.behaviour.sim2;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;
import se.kth.app.sim.behaviour.BehaviourCreator;

/**
 * Created by Nick on 5/22/2017.
 */
public class QBehaviour implements Behaviour {

    @Override
    public void setup(Evaluator eval) {
        eval.receive(BehaviourCreator.EVAL_UNIQUE_ID, BehaviourCreator.getSimulation2Setup());

        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("colors")).evalCmd(new Cmd.Assign(Val.EmptyMap$.EmptyMap$.MODULE$));

        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("colors")).evalExpr(new Expr.Get("green")).evalCmd(new Cmd.Assign(new Val.Str("#00ff00")));

        String json = eval.toJsonString();

        System.out.println(json);
    }
}
