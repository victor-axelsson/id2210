package se.kth.app.sim.behaviour.sim5;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;

/**
 * Created by victoraxelsson on 2017-05-25.
 */
public class QBehaviour implements Behaviour {
    @Override
    public void setup(Evaluator eval) {

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("a"))
                .evalCmd(new Cmd.Assign(Val.EmptyList$.MODULE$));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("a"))
                .evalExpr(new Expr.Idx(0))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("z")));

    }
}
