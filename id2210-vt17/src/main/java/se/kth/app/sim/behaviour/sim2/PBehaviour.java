package se.kth.app.sim.behaviour.sim2;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public class PBehaviour implements Behaviour {

    @Override
    public void setup(Evaluator eval) {
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("colors")).evalExpr(new Expr.Get("blue")).evalCmd(new Cmd.Assign(new Val.Str("#0000ff")));

        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("colors")).evalExpr(new Expr.Get("red")).evalCmd(new Cmd.Assign(new Val.Str("#ff0000")));

        System.out.println(eval.toJsonString());
    }

}
