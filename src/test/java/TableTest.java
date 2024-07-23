import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Hashtable;
import java.util.Optional;

/**
 *
 * HashTable 与 HashMap区别 https://blog.csdn.net/weixin_58724261/article/details/131096333
 *
 * @author manji
 * Created on 2024/1/5 17:13
 */
public class TableTest {

    @Test
    public void hashTableTest() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("a", 1);
        hashtable.put("b", 2);
        System.out.println(hashtable);
        String a = "9991710294306017";
        System.out.println(Long.valueOf(a));
    }

    @Test
    public void tableTest() {
        Table<String,String,String> routerTable = HashBasedTable.create();
        routerTable.put("type", "a", "1");
        routerTable.put("type", "b", "2");
        routerTable.put("type", "c", "3");

        routerTable.put("type-c", "a", "3");
        routerTable.put("type-c", "b", "3");
        routerTable.put("type-c", "c", "3");
        System.out.println(routerTable);
    }
}
