public class IntCompTest {
    public static void main(String[] args) {
        int a = (int) Math.pow(2, 18) - 1;
        int b = (int) Math.pow(2, 14) - 1;

        int comp = (a << 14) | b;
        int ca = comp >>> 14;
        int cb = comp & 0x3FFF;

        System.out.println(a);
        System.out.println(b);
        System.out.println(ca);
        System.out.println(cb);
    }
}
