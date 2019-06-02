package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;
import org.junit.Test;


public class MultiAndSumFlow implements FlowableWork {


    private static Logger logger = Logger.getLogger(MultiAndSumFlow.class.getName());

    public void async1() {
        logger.debug("** invoke - async1");
        TestUtil.waiting(500);
        logger.debug("** invoke - async1 - END");
    }

    public void async2() {
        logger.debug("** invoke - async2");
        TestUtil.waiting(600);
        logger.debug("** invoke - async2 - END");
    }


    public void sum1() {
        logger.debug("** invoke - sum1");
        TestUtil.waiting(300);
        logger.debug("** invoke - sum1 - END");
    }

    public void async3() {
        logger.debug("** invoke - async3");
        TestUtil.waiting(500);
        logger.debug("** invoke - async1 - END");
    }

    public void async4() {
        logger.debug("** invoke - async4");
        TestUtil.waiting(600);
        logger.debug("** invoke - async2 - END");
    }


    public void sum2() {
        logger.debug("** invoke - sum2");
        TestUtil.waiting(300);
        logger.debug("** invoke - sum2 - END");
    }



    public void defineWorkFlow(WorkFlow flow) {

        flow.runAsync(this::async1,"h1").runAsync(this::async2,"h2");

        flow.waitAll("h1","h2").next(this::sum1);

        flow.runAsync(this::async3,"h3").runAsync(this::async4,"h4");

        flow.waitAll("h3","h4").next(this::sum2).finish();

    }


    @Test
    public void test() throws InterruptedException {
        WorkSchedule schedule = start();

        schedule.waitUntilFinish();

        WorkProcessorFactory.getProcessor().awaitShutdown();


    }


}
