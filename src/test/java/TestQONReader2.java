import com.qow.util.qon.QONArray;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestQONReader2 {
    public static void main(String[] args) throws UntrustedQONException, IOException {
        File file = new File("src/test/resources/test2.qon");
        QONObject pr = new QONObject(file);

        String[] targets = {"string", "int", "object", "list", "404"};
        for (String target : targets) {
            System.out.printf("%s:%s\n", target, pr.get(target));
        }

        QONObject son = pr.getQONObject("object");
        String[] targets2 = {"string2", "int2", "string", "404"};
        for (String target : targets2) {
            System.out.printf("%s:%s\n", target, son.get(target));
        }

        QONArray qonArray = pr.getQONArray("list");
        for (int i = 0; i < qonArray.list().length; i++) {
            System.out.printf("%d:%s\n", i, qonArray.list()[i]);
        }

    }
}
