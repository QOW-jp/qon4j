import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONArray;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestQONReader8 {
    public TestQONReader8(String pathname) throws UntrustedQONException, IOException, NoSuchKeyException {
        File file = new File(pathname);

        QONObject qon = null;
        long startMilliTime = System.currentTimeMillis();
        qon = new QONObject(file);
        long goalMilliTime = System.currentTimeMillis();
        System.out.println(goalMilliTime - startMilliTime + "ms");

        String[] targets = {"same", "number", "altnumber"};
        print(qon, targets, "");

        QONObject first = qon.getQONObject("first");
        String[] targets2 = {"same", "a", "b", "c"};
        print(first, targets2, "first_");

        QONObject second = first.getQONObject("second");
        String[] targets3 = {"same", "d", "e", "f"};
        print(first, targets2, "first_second_");

        QONArray list = qon.getQONArray("list");
        for (String member : list.list()) {
            System.out.println("list:" + member);
        }
    }

    public static void main(String[] args) throws UntrustedQONException, IOException, NoSuchKeyException {
        new TestQONReader8("src/test/resources/test8.qon");
    }

    public void print(QONObject qon, String[] targets, String pre) throws NoSuchKeyException {
        for (String target : targets) {
            System.out.printf("%s%s=%s\n", pre, target, qon.get(target));
        }
    }
}
