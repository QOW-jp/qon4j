import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class TestPropertiesReader {
    public static void main(String[] args) throws UntrustedQONException, IOException {
        File file = new File("src/main/resources/test1.qon");
        QONObject pr = new QONObject(file);
        String target = "string";
        System.out.printf("%s:%s",target,pr.getProperty(target));


    }
}
