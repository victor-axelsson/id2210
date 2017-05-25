package se.kth.app.sim.behaviour.nested_maps;

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

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("map1"))
                .evalExpr(new Expr.Get("map2"))
                .evalExpr(new Expr.Get("innerRegistry"))
                .evalCmd(new Cmd.Assign(new Val.Str("innerRegistryVal")));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("map1"))
                .evalExpr(new Expr.Get("outerRegistry"))
                .evalCmd(new Cmd.Assign(new Val.Str("outerRegistryVal")));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("map1"))
                .evalExpr(new Expr.Get("map2"))
                .evalExpr(new Expr.Get("innerList"))
                .evalExpr(new Expr.Idx(0))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("Value at first index")));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("map1"))
                .evalExpr(new Expr.Get("map2"))
                .evalExpr(new Expr.Get("innerList"))
                .evalExpr(new Expr.Idx(1))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("Value at second index")));

    }
}
