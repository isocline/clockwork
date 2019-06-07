package isocline.clockwork.dummy;

import isocline.clockwork.WorkProcessor;
import org.junit.Test;

import java.util.stream.Stream;

/**
 * @author
 */
public class CompletableFutureTest4 {


    @Test
    public void thenCombineTest() throws Exception {
        Price price = new Price();

        System.out.println("---1");

        WorkProcessor.main()
                .reflow(flow -> {
                    flow
                            .mapAsync(e -> price.calculatePrice(1),
                                    e -> price.calculatePrice(2))

                            .next(e -> {
                                Stream<Double> list = e.getStream();
                                return list.mapToDouble(i -> i).sum();

                            });
                })
                .activate(System.out::println);

        System.out.println("---2");


        WorkProcessor.main().awaitShutdown();

    }

    static class Price {
        public void print() {
            System.out.println("START");
        }

        public double getPrice(double oldprice) throws Exception {
            return calculatePrice(oldprice);
        }

        public double calculatePrice(double oldprice) throws Exception {
            System.out.println("Input :" + oldprice);
            Thread.sleep(1000l);
            System.out.println("Output :" + (oldprice + 1l));
            return oldprice + 1l;
        }


    }
}