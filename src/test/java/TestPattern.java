import java.util.regex.Pattern;

public class TestPattern {
    public static void main(String[] args) {
        System.out.println();
        String a = "$()";
        System.out.println(a);
        System.out.println("\\$\\(\\)");
        System.out.println(Pattern.quote(a));


        String VARIABLE_START = Pattern.quote("$(");
        String VARIABLE_END = Pattern.quote(")");
        Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_START + "([^)]*)" + VARIABLE_END);
        Pattern VARIABLE_PATTERN_OLD = Pattern.compile("\\$\\(([^)]*)\\)");

        String b = "kl$(DSAT)jk";
        String b2 = "\\$\\(DSAT\\)";
        String c = VARIABLE_START+"dsds"+VARIABLE_END+"dsadsa";
//        System.out.println(VARIABLE_PATTERN.matcher(b).group(1));
//        System.out.println(VARIABLE_PATTERN.matcher(c).group(1));
        System.out.println(VARIABLE_PATTERN_OLD.matcher(b).group(1));
        System.out.println(VARIABLE_PATTERN_OLD.matcher(c).group(1));
        System.out.println();
        System.out.println();

    }
}
