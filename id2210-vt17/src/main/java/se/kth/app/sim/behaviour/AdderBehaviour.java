package se.kth.app.sim.behaviour;

import app.document.evaluator.Evaluator;
import app.document.evaluator.Mutation;
import app.document.language.*;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public class AdderBehaviour implements Behaviour {

    @Override
    public void actOnIt(Evaluator eval) {



        Cmd cmd = new Cmd.Assign(new Val.Str("Val"));
        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("someVar")).evalExpr(new Expr.Get("someVar2")).evalCmd(cmd);

        String json = eval.toJsonString();

        System.out.println(json);
    }
}
