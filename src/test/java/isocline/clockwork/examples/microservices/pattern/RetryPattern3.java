package isocline.clockwork.examples.microservices.pattern;

import isocline.clockwork.TestUtil;
import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkFlow;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.log.XLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RetryPattern3 {


    private XLogger logger = XLogger.getLogger(RetryPattern3.class);

    public void init() {
        logger.debug("init");
    }

    public void callService1(WorkEvent e) {
        logger.debug("Service1 - start");
        TestUtil.waiting(2000);
        logger.debug("Service1 - end");

        e.origin().setAttribute("result:service1", "A");
        throw new RuntimeException("zzzz");
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

        WorkProcessor.main().reflow(flow -> {

            flow.check(e -> {
                if (e.count() == 4) return false;
                return true;
            })
                    .next(this::callService1, WorkFlow.FINISH)
                    .fireEventOnError(WorkFlow.START, 2000);


            flow.onError("*").next(this::onError);

            flow.onError(RuntimeException.class).next(this::onError2);


        }).run();


    }
}
