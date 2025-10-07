import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestQONReader1 {
    public static void main(String[] args) throws UntrustedQONException, IOException, NoSuchKeyException {
        File file = new File("src/test/resources/test1.qon");
        QONObject pr = new QONObject(file);

        String[] targets = {"string", "int", "long", "double", "null","boolean","404"};
        for (String target : targets) {
            System.out.printf("%s:%s\n", target, pr.get(target));
        }
    }
}
