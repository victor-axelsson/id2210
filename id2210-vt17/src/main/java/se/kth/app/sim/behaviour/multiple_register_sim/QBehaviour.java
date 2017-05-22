package se.kth.app.sim.behaviour.multiple_register_sim;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;

/**
 * Created by Nick on 5/22/2017.
 */
public class QBehaviour implements Behaviour {
    @Override
    public void actOnIt(Evaluator eval) {
        setup(eval);

        //When used in conjunction with PBehaviour should produce a multiple register
        Cmd cmd = new Cmd.Assign(new Val.Str("C"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("key")).evalCmd(cmd);

        String json = eval.toJsonString();

        System.out.println(json);
    }

    @Override
    public void setup(Evaluator eval) {
        Cmd setup = new Cmd.Assign(new Val.Str("A"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("key")).evalCmd(setup);
    }
}
