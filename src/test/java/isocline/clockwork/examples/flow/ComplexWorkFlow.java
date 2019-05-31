package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;
import org.junit.Test;


public class ComplexWorkFlow implements FlowableWork {


    private static Logger logger = Logger.getLogger(ComplexWorkFlow.class.getName());

    public void order() {
        logger.debug("** invoke - order");

    }


    public void sendSMS() {
        logger.debug("invoke - sendSMS ** start");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        logger.debug("invoke - sendSMS ** end");


    }

    public void sendMail() {
        logger.debug("invoke - sendMail start");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        logger.debug("invoke - sendMail end");

    }


    public void report() {
        logger.debug("invoke - report");

    }

    public void report2(WorkEvent event) {
        logger.debug("invoke - report2 " + event.getEventName() + " "+event);

    }

    public void defineWorkFlow(WorkFlow flow) {

        flow.runAsync(this::order).next(this::sendMail, "h1");
        flow.runAsync(this::sendSMS).next(this::report, "h2");

        flow.waitAll("h1","h2").next(this::report2).finish();

    }


    @Test
    public void test() throws InterruptedException {
        WorkSchedule schedule = start();

        schedule.waitUntilFinish();

        WorkProcessorFactory.getProcessor().awaitShutdown();


    }


}
