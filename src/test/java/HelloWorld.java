import java.util.ArrayList;
import java.util.List;

/**
 * Created by PP on 2016/3/26.
 */
public class HelloWorld {


    public static void main(String agrs[]){
        System.out.println("HelloWorld!");
        String a = "com/wuming";
        String str = new String("com/wuming");
        String string = "com/wuming";
        System.out.println(a + "----内存地址----" + System.identityHashCode(a));
        System.out.println(str + "----内存地址----" + System.identityHashCode(str));
        System.out.println(string + "----内存地址----" + System.identityHashCode(string));

        int b = Integer.parseInt("11");
        int c = 10;
        System.out.println(b + "----内存地址----" + System.identityHashCode(b));
        System.out.println(c + "----内存地址----" + System.identityHashCode(c));

        String arr[] = {"1", "a", "b", "abc"};
        arr[0] = "6";
        System.out.println(arr.length + "-----" + arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "  ");

        }

        List<String> aList = new ArrayList();
        ArrayList<Integer> bList = new ArrayList<>();
        System.out.println(aList instanceof ArrayList);
        System.out.println(bList instanceof ArrayList);


    }
}













