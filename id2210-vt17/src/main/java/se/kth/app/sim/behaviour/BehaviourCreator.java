package se.kth.app.sim.behaviour;

import app.document.cursor.Cursor;
import app.document.cursor.Key;
import app.document.evaluator.Evaluator;
import app.document.evaluator.Operation;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import java.util.ArrayList;

/**
 * Created by victoraxelsson on 2017-05-23.
 */
public class BehaviourCreator {

    private static final int EVAL_UNIQUE_ID = 999;

    private static Cursor getEmptyCursor(){
        Expr.Doc doc = new Expr.Doc();
        Key.RootMapT key = new Key.RootMapT(doc);
        List<Key> keys = List$.MODULE$.empty();
        return new Cursor(keys, key);
    }

    public static Operation getSimulation1Setup(){

        Cursor curs = getEmptyCursor();
        Key.regT key = new Key.regT("key");
        curs = curs.append(key);

        return new Evaluator(EVAL_UNIQUE_ID).makeAssign(curs, new Val.Str("#0000ff"));
    }

    public static Operation getSimulation2Setup(){

        Cursor curs = getEmptyCursor();
        Key.mapT addMap = new Key.mapT("colors");
        Key.regT blue = new Key.regT("blue");

        curs = curs.append(addMap);
        curs = curs.append(blue);

        return new Evaluator(EVAL_UNIQUE_ID).makeAssign(curs, new Val.Str("#0000ff"));
    }

    public static Operation getSimulation3Setup() {
        Cursor curs = getEmptyCursor();
        Key.listT addList = new Key.listT("grocery");
        curs = curs.append(addList);

        return new Evaluator(EVAL_UNIQUE_ID).makeAssign(curs, Val.EmptyList$.MODULE$);
    }

    public static java.util.List<Operation> getSimulation4Setup() {

        Evaluator eval = new Evaluator(EVAL_UNIQUE_ID);
        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("items"))
                .evalExpr(new Expr.Idx(0))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("a")));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("items"))
                .evalExpr(new Expr.Idx(1))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("b")));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("items"))
                .evalExpr(new Expr.Idx(2))
                .evalCmd(new Cmd.InsertAfter(new Val.Str("c")));


        return eval.send();
    }

    public static java.util.List<Operation> getSimulation6Setup() {

        Evaluator eval = new Evaluator(EVAL_UNIQUE_ID);
        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("todo"))
                .evalExpr(new Expr.Idx(0))
                .evalCmd(new Cmd.InsertAfter(Val.EmptyMap$.MODULE$));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("todo"))
                .evalExpr(new Expr.Idx(0))
                .evalExpr(new Expr.Get("title"))
                .evalCmd(new Cmd.Assign(new Val.Str("buy milk")));

        eval.evalExpr(new Expr.Doc())
                .evalExpr(new Expr.Get("todo"))
                .evalExpr(new Expr.Idx(0))
                .evalExpr(new Expr.Get("done"))
                .evalCmd(new Cmd.Assign(Val.False$.MODULE$));

        return eval.send();
    }

    public static java.util.List<Operation> getSimulation5Setup() {
        throw new RuntimeException("There is no pre setup for this scenario");
    }
}
