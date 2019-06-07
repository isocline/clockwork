package isocline.clockwork.examples.microservices.pattern;

import isocline.clockwork.TestUtil;
import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.Plan;
import isocline.clockwork.check.CircuitBreaker;
import isocline.clockwork.log.XLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CircuitBreaker1 {

    private static int CNT = 0;

    private XLogger logger = XLogger.getLogger(CircuitBreaker1.class);

    public void init() {
        logger.debug("init");
    }

    public void callService1(WorkEvent e) {
        CNT++;

        logger.debug("Service1 - start " + CNT);
        TestUtil.waiting(100);
        logger.debug("Service1 - end " + CNT);

        e.origin().setAttribute("result:service1", "A");

        if (CNT > 2 && CNT < 7) {
            logger.debug("Service1 - wait " + CNT);
            TestUtil.waiting(3000);

            throw new RuntimeException("connect fail");
        }

    }


    public void finish(WorkEvent e) {
        logger.debug("finish start " + Thread.currentThread().getId());

        logger.debug(e.origin());
        logger.debug(e.origin().getAttribute("result:service1"));

        String result = e.origin().getAttribute("result:service1").toString()
                + e.origin().getAttribute("result:service2")
                + e.origin().getAttribute("result:service3");

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

        CircuitBreaker circuitBreaker = CircuitBreaker.create("xhk");
        circuitBreaker.setMaxFailCount(3);

        Plan schedule =
                WorkProcessor.main()
                        .reflow(flow -> {


                            String cursor = flow.fireEvent("error::timeout", 3000)
                                    .check(circuitBreaker::check)
                                    .next(this::callService1).cursor();

                            flow.finish();


                            flow.onError(cursor).next(circuitBreaker::error).finish();


                            //flow.onError("*").next(this::onError2).finish();


                            flow.wait("test").finish();
                        });



        schedule.run();


    }

    @Test
    public void testMulti() {
        for (int i = 0; i < 15; i++) {

            CircuitBreaker1 test = new CircuitBreaker1();
            try {
                test.startTest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
