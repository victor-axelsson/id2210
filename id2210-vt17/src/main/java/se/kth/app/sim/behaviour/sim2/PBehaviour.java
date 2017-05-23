package se.kth.app.sim.behaviour.sim2;

import app.document.Doc;
import app.document.cursor.Cursor;
import app.document.cursor.Key;
import app.document.evaluator.Evaluator;
import app.document.evaluator.Operation;
import app.document.language.Cmd;
import app.document.language.Expr;
import app.document.language.Val;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import se.kth.app.sim.behaviour.Behaviour;

/**
 * Created by victoraxelsson on 2017-05-19.
 */
public class PBehaviour implements Behaviour {

    public static Cursor curs;

    static {
        Expr.Doc doc = new Expr.Doc();
        Key.RootMapT key = new Key.RootMapT(doc);
        List<Key> keys = List$.MODULE$.empty();
        curs = new Cursor(keys, key);

        Key.mapT addMap = new Key.mapT("colors");
        Key.regT blue = new Key.regT("blue");

        curs = curs.append(addMap);
        curs = curs.append(blue);

    }

    public static Operation operation = new Evaluator(999).makeAssign(curs, new Val.Str("#0000ff"));

    @Override
    public void setup(Evaluator eval) {
        eval.receive(operation);

        eval.evalExpr(new Expr.Doc()).evalExpr(new Expr.Get("colors")).evalExpr(new Expr.Get("red")).evalCmd(new Cmd.Assign(new Val.Str("#ff0000")));

        System.out.println(eval.toJsonString());
    }

}
