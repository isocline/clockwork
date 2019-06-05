package isocline.clockwork.examples.microservices.pattern;

import isocline.clockwork.TestUtil;
import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.log.XLogger;
import isocline.clockwork.pattern.CircuitBreaker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CircuitBreaker2 {

    private static int CNT = 0;

    private XLogger logger = XLogger.getLogger(CircuitBreaker2.class);

    public void init() {
        logger.debug("init");
    }

    public void callService1(WorkEvent e) {
        CNT++;

        logger.debug("Service1 - start " + CNT);
        TestUtil.waiting(100);
        logger.debug("Service1 - end " + CNT);

        e.root().setAttribute("result:service1", "A");

        if (CNT > 2 && CNT < 7) {
            logger.debug("Service1 - wait " + CNT);
            TestUtil.waiting(3000);

            throw new RuntimeException("connect fail");
        }

    }


    public void finish(WorkEvent e) {
        logger.debug("finish start " + Thread.currentThread().getId());

        logger.debug(e.root());
        logger.debug(e.root().getAttribute("result:service1"));

        String result = e.root().getAttribute("result:service1").toString()
                + e.root().getAttribute("result:service2")
                + e.root().getAttribute("result:service3");

        assertEquals("ABC", result);

        logger.debug("finish - " + result);
    }


    public void onTimeout(WorkEvent e) {
        logger.debug("timeout  " + e.getEventName());
    }

    public void onError(WorkEvent e) throws Throwable {

        logger.debug("error " + e.getEventName());

        Throwable err = e.getThrowable();
        if (err != null) {
            err.printStackTrace();
            throw err;
        } else {
            throw new RuntimeException("timeout");
        }
    }


    public void onError2(WorkEvent e) throws IllegalArgumentException {

        logger.debug("error2 " + e.getEventName());

        Throwable err = e.getThrowable();
        if (err != null) {
            err.printStackTrace();

        } else {
            //throw new RuntimeException("timeout");
        }
    }

    public static int count = 0;


    public void startTest() {


        WorkProcessor
                .main()
                .reflow(flow -> {

                    flow.pattern(
                            CircuitBreaker.create("xdR"), () -> {
                                flow.next(this::callService1);
                            }


                    );

                }).run();


    }

    @Test
    public void testMulti() {
        for (int i = 0; i < 10; i++) {

            CircuitBreaker2 test = new CircuitBreaker2();
            try {
                test.startTest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
