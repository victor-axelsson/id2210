package se.kth.app.sim.behaviour.sim4;

import app.document.evaluator.Evaluator;
import app.document.evaluator.Operation;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;
import se.kth.app.sim.behaviour.BehaviourCreator;

import java.util.List;

/**
 * Created by victoraxelsson on 2017-05-23.
 */
public class PBehaviour implements Behaviour {
    @Override
    public void setup(Evaluator eval) {

        List<Operation> ops =  BehaviourCreator.getSimulation4Setup();

        for(int i = 0; i < ops.size(); i++){
            eval.receive(ops.get(i));
        }

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("items"))
                .evalExpr(new Expr.Idx(1))
                .evalCmd(new Cmd.Delete());

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("items"))
                .evalExpr(new Expr.Idx(1))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("x")));

        System.out.println(eval.toJsonString());

    }
}
