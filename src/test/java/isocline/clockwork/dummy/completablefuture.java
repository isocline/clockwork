package isocline.clockwork.dummy;


import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author jibumjung
 */
public class completablefuture {

    Runnable task = () -> {
        try {
            TimeUnit.SECONDS.sleep(3l);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("TASK completed");
    };


    public String test() {
        System.err.println("xx");
        return "z";
    }
    @Test
    public void completableFuture() throws Exception {
        CompletableFuture
                .runAsync(this::test)

                .thenCompose(aVoid -> CompletableFuture.runAsync(task))
                .thenAcceptAsync(aVoid -> System.out.println("all tasks completed!!"))
                .exceptionally(throwable -> {
                    System.out.println("exception occurred!!");
                    return null;
                });
        try {
            TimeUnit.SECONDS.sleep(15l);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}