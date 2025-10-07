import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONArray;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestQONReader5 {
    public static void main(String[] args) throws UntrustedQONException, IOException, NoSuchKeyException {
        File file = new File("src/test/resources/test5.qon");
        QONObject pr = new QONObject(file);

        String[] targets = {"string", "int", "object", "list", "404"};
        for (String target : targets) {
            System.out.printf("%s=%s\n", target, pr.get(target));
        }

        QONObject object = pr.getQONObject("object");
        String[] targets2 = {"string2", "int2", "string", "404"};
        for (String target : targets2) {
            System.out.printf("ob_%s=%s\n", target, object.get(target));
        }

        QONObject son = object.getQONObject("son_object");
        String[] targets3 = {"string3", "int3", "string", "404"};
        for (String target : targets3) {
            System.out.printf("son_%s=%s\n", target, son.get(target));
        }

        QONArray qonArray = pr.getQONArray("list");
        for (int i = 0; i < qonArray.list().length; i++) {
            System.out.printf("%d:%s\n", i, qonArray.list()[i]);
        }
    }
}
