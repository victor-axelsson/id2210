import app.document.Doc;
import app.document.DocItem;
import app.document.TReg;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by victoraxelsson on 2017-05-03.
 */
public class DocTest {

    @Test
    public void testToString(){
        Doc d = new Doc();
        Assert.assertEquals("{}", d.toString());
    }

    @Test
    public void testInsert(){
        Doc d = new Doc();
        d.get("key1").assign(new TReg("someVal"));
        Assert.assertEquals("{\"key1\": \"someVal\"}", d.toString());
    }

    @Test
    public void testInsertMutliple(){
        Doc d = new Doc();
        d.get("key1").assign(new TReg("someVal"));
        d.get("key2").assign(new TReg("someVal"));
        Assert.assertEquals("{\"key1\": \"someVal\", \"key2\": \"someVal\"}", d.toString());
    }


    @Test
    public void testInsertNested(){
        Doc d = new Doc();
        d.get("key1").get("key2").assign(new TReg("someVal"));
        Assert.assertEquals("{\"key1\": {\"key2\": \"someVal\"}}", d.toString());
    }

    @Test
    public void testInsertArray(){
        Doc d = new Doc();
        d.get("key1").assign(new TReg("[]"));
        Assert.assertEquals("{\"key1\": []}", d.toString());
    }


    @Test
    public void testInserArrayOfMaps(){
        Doc d = new Doc();
        DocItem item = d.get("key1");
        item.assign(new TReg("[]"));
        item.get("key1").insertAfter("someItem");
        Assert.assertEquals("{\"key1\": [\"someItem\"]}", d.toString());
    }

}
