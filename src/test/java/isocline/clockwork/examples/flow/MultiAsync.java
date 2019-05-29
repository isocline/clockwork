package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;
import org.junit.Test;


public class MultiAsync implements FlowableWork {


    private static Logger logger = Logger.getLogger(MultiAsync.class.getName());

    public void asyncMulti() {
        logger.debug("** invoke - async1");
        TestUtil.waiting(500);
        logger.debug("** invoke - async1 - END");
    }

    public void sum1() {
        logger.debug("** invoke - sum1");
        TestUtil.waiting(300);
        logger.debug("** invoke - sum1 - END");
    }




    public void defineWorkFlow(WorkFlow flow) {

        flow.runAsync(this::asyncMulti,5);

        flow.waitAll().next(this::sum1);



    }


    @Test
    public void test() throws InterruptedException {
        WorkSchedule schedule = start();

        schedule.waitUntilFinish();

        WorkProcessorFactory.getDefaultProcessor().awaitShutdown();


    }


}
