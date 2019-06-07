package isocline.clockwork.dummy;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @author jibumjung
 */
public class CompletableFutureTest {

    // 두개의 비동기 요청을 동시에 진행해서 조합 할 수 있다.
    @Test
    public void thenCombineTest() throws Exception {
        Price price = new Price();
        CompletableFuture<Double> price1 = price.getPriceAsync(1);
        CompletableFuture<Double> price2 = price.getPriceAsync(2);
        price2.thenCombineAsync(price1, (a, b) -> a + b)
                .thenAcceptAsync(System.out::print);

        System.out.println("Non Blocking!!");

        // main thread 가 죽으면 child 도 다 죽어 버려서 대기함.
        Thread.sleep(5000l);
    }

    static class Price {

        public void test() {

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

        public CompletableFuture<Double> getPriceAsync(double oldPrice) {
            CompletableFuture<Double> completableFuture = new CompletableFuture<>();
            new Thread(() -> {
                try {
                    double price = calculatePrice(oldPrice);
                    completableFuture.complete(price);
                } catch (Exception ex) {
                    completableFuture.completeExceptionally(ex);
                }
            }).start();

            return completableFuture;
        }
    }
}