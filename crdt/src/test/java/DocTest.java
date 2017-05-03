import app.document.Doc;
import app.document.DocItem;
import app.document.TMap;
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
    public void testInsertEmptyArray(){
        Doc d = new Doc();
        d.get("key1").assign(new TReg("[]"));
        Assert.assertEquals("{\"key1\": []}", d.toString());
    }


    @Test
    public void testInsertHeadArray(){
        Doc d = new Doc();
        DocItem item = d.get("key1");
        item.assign(new TReg("[]"));
        item.get("key1").idx(0).insertAfter(new TReg("someItem"));
        Assert.assertEquals("{\"key1\": [\"someItem\"]}", d.toString());
    }


    @Test
    public void testInsertMultipleHeadArray(){
        Doc d = new Doc();
        DocItem item = d.get("key1");
        item.assign(new TReg("[]"));
        DocItem list = item.get("key1").idx(0);
        list.insertAfter(new TReg("someItem1"));
        list.insertAfter(new TReg("someItem2"));
        list.insertAfter(new TReg("someItem3"));
        Assert.assertEquals("{\"key1\": [\"someItem3\", \"someItem2\", \"someItem1\"]}", d.toString());
    }

    @Test
    public void testInsertMapIntoArray(){
        Doc d = new Doc();
        DocItem list = d.get("list1");
        TMap map = new TMap();
        map.get("mapKey1").assign(new TReg("mapVal1"));
        map.get("mapKey2").assign(new TReg("mapVal2"));
        map.get("mapKey3").assign(new TReg("mapVal3"));
        list.idx(0).insertAfter(new TReg(map));
        list.idx(0).insertAfter(new TReg(map));
        
        Assert.assertEquals("{\"key1\": [{\"mapKey1\":\"mapVal1\", \"mapKey2\":\"mapVal2\", \"mapKey3\":\"mapVal3\"},{\"mapKey1\":\"mapVal1\", \"mapKey2\":\"mapVal2\", \"mapKey3\":\"mapVal3\"}]}", d.toString());
    }

}
