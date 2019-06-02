package isocline.clockwork.dummy;

import java.util.Arrays;
import java.util.function.Consumer;

public class TestSplit {

    public static void main(String[] args) throws Exception {

        String txt="123&345,678";

        String[] lists = txt.split("&");

        for(String x:lists) {
            System.out.println(x);
        }

        Arrays.asList("1","2","3").stream()

                .forEach(System.out::println); // 1,4,9

        /*
        Arrays.asList("1","2","3").stream()

                .forEach(TestSplit::test); // 1,4,9
                */


        TestSplit  s = new TestSplit();
        s.forEach(s::test);

    }

    public void forEach(Consumer<String> action) {
        action.accept(null);

    }

    public void test(String x) {
        System.err.println(">--> "+x);
    }
}
