package isocline.clockwork.examples.microservices.pattern;

import isocline.clockwork.TestUtil;
import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.log.XLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RetryPattern2 {


    private XLogger logger = XLogger.getLogger(RetryPattern2.class);

    public void init() {
        logger.debug("init");
    }

    public void callService1(WorkEvent e) {
        logger.debug("Service1 - start");
        TestUtil.waiting(500);
        logger.debug("Service1 - end");

        e.root().setAttribute("result:service1", "A");
        throw new RuntimeException("zzzz");
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

    public void onError(WorkEvent e) {

        logger.debug("error " + e.getEventName());

        Throwable err = e.getThrowable();
        if (err != null) {
            err.printStackTrace();
        }
    }


    public void onError2(WorkEvent e) {

        logger.debug("error2 " + e.getEventName());

        Throwable err = e.getThrowable();
        if (err != null) {
            err.printStackTrace();
        }
    }

    public static int count = 0;


    @Test
    public void startTest() {


        WorkProcessor.main()
                .reflow(flow -> {

                    flow

                            .next(this::callService1)
                            .retryOnError(3, 2000);


                }).run();


        TestUtil.waiting(10000);
    }
}
