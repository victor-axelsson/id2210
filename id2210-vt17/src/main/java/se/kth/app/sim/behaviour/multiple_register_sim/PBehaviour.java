package se.kth.app.sim.behaviour.multiple_register_sim;

import app.document.evaluator.Evaluator;
import app.document.language.*;
import se.kth.app.sim.behaviour.Behaviour;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.List;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public class PBehaviour implements Behaviour {

    @Override
    public void setup(Evaluator eval) {
        Cmd setup = new Cmd.Assign(new Val.Str("A"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("key")).evalCmd(setup);

        Cmd cmd2 = new Cmd.Assign(new Val.Str("B"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("key")).evalCmd(cmd2);
        System.out.println(eval.toJsonString());
    }

}
