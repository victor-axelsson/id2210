package se.kth.app.sim.behaviour;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;

/**
 * Created by Nick on 5/22/2017.
 */
public class AssignBehaviour implements Behaviour {
    @Override
    public void actOnIt(Evaluator eval) {
        //When used in conjunction with AdderBehaviour should produce a multiple register
        Cmd cmd = new Cmd.Assign(new Val.Str("Val2"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("someVar")).evalExpr(new Expr.Get("someVar2")).evalCmd(cmd);

        String json = eval.toJsonString();

        System.out.println(json);
    }
}
