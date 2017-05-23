package se.kth.app.sim.behaviour;

import app.document.cursor.Cursor;
import app.document.cursor.Key;
import app.document.evaluator.Evaluator;
import app.document.evaluator.Operation;
import app.document.language.Expr;
import app.document.language.Val;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;

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
}
