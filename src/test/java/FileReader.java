import java.io.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by PP on 2016/4/1.
 */
public class FileReader {
    private String name;
    private String id;

    public static void main(String agrs[]) {

        File file = new File("file/test.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            OutputStreamWriter isr = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(isr);
            String text = "Hello, I'am wuming, nice to meet you!";
            bw.write(text);
            bw.flush();
            bw.close();
            isr.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
