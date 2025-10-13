import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONArray;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestQONReader7 {
    public TestQONReader7(String pathname) throws UntrustedQONException, IOException, NoSuchKeyException {
        File file = new File(pathname);
        int trials = 10000;
        QONObject qon = null;
        long startMilliTime = System.currentTimeMillis();
        for (int i = 0; i < trials; i++) {
            qon = new QONObject(file);
        }
        long goalMilliTime = System.currentTimeMillis();
        System.out.println(goalMilliTime - startMilliTime + "ms");

        String[] targets = {"first", "middle", "second", "space", "sp ce", "separate"};
        print(qon, targets);

        String[] tar2 = {"1st", "total", "sep"};
        print(qon, tar2);

        QONObject object = qon.getQONObject("object");
        String[] targets2 = {"son"};
        for (String target : targets2) {
            System.out.printf("ob_%s=%s\n", target, object.get(target));
        }

//        QONObject son = object.getQONObject("son_object");
//        String[] targets3 = {"sonson"};
//        for (String target : targets3) {
//            System.out.printf("son_%s=%s\n", target, son.get(target));
//        }

        QONArray list = qon.getQONArray("list");
        for (String member : list.list()) {
            System.out.println("list:" + member);
        }
    }

    public static void main(String[] args) throws UntrustedQONException, IOException, NoSuchKeyException {
        new TestQONReader7("src/test/resources/test7.qon");
    }

    public void print(QONObject qon, String[] targets) throws NoSuchKeyException {
        for (String target : targets) {
            System.out.printf("%s=%s\n", target, qon.get(target));
        }
    }
}
