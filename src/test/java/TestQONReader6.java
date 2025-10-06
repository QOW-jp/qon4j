import com.qow.util.qon.QONArray;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestQONReader6 {
    public static void main(String[] args) throws UntrustedQONException, IOException {
        File file = new File("src/test/resources/test6.qon");
        QONObject qon = new QONObject(file);

        String[] targets = {"first", "middle", "second","space", "separate"};
        print(qon, targets);

        String[] tar2 = {"1st", "total", "sep"};
        print(qon, tar2);

        QONObject object = qon.getQONObject("object");
        String[] targets2 = {"son"};
        for (String target : targets2) {
            System.out.printf("ob_%s=%s\n", target, object.get(target));
        }

        QONObject son = object.getQONObject("son_object");
        String[] targets3 = {"sonson"};
        for (String target : targets3) {
            System.out.printf("son_%s=%s\n", target, son.get(target));
        }

        QONArray list = qon.getQONArray("list");
        for (String member : list.list()) {
            System.out.println("list:" + member);
        }
    }

    public static void print(QONObject qon, String[] targets) {
        for (String target : targets) {
            System.out.printf("%s=%s\n", target, qon.get(target));
        }
    }
}
