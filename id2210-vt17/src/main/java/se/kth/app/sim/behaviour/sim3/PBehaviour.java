package se.kth.app.sim.behaviour.sim3;

import app.document.evaluator.Evaluator;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import se.kth.app.sim.behaviour.Behaviour;
import se.kth.app.sim.behaviour.BehaviourCreator;

/**
 * Created by victoraxelsson on 2017-05-23.
 */
public class PBehaviour implements Behaviour {

    @Override
    public void setup(Evaluator eval) {
        eval.receive(BehaviourCreator.getSimulation3Setup());

        //Insert the eggs
        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("grocery"))
                .evalExpr(new Expr.Idx(0))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("eggs")));


        //Insert the ham
        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("grocery"))
                .evalExpr(new Expr.Idx(0))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("ham")));



    }
}
