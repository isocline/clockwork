package isocline.clockwork.dummy;

import isocline.clockwork.WorkEvent;

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


        TestSplit  s = new TestSplit();
        s.forEach((WorkEvent z)-> z. );
        */
    }

    public void forEach(Consumer<WorkEvent>  action) {

    }

    public static void test(WorkEvent x) {
        System.err.println(">> "+x);
    }
}
