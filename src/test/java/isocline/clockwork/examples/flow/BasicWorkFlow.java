package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;
import org.junit.Test;


public class BasicWorkFlow implements FlowableWork {


    private static Logger logger = Logger.getLogger(BasicWorkFlow.class.getName());

    public void order() {
        logger.debug("invoke - order");

    }


    public void sendSMS() {
        logger.debug("invoke - sendSMS start");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        logger.debug("invoke - sendSMS end");


    }

    public void sendMail() {
        logger.debug("invoke - sendMail start");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        logger.debug("invoke - sendSMS end");

    }


    public void report() {
        logger.debug("invoke - report");

    }

    public void defineWorkFlow(WorkFlow flow) {

        flow.run(this::order).next(this::sendMail).next(this::sendSMS).next(this::report).finish();


    }


    @Test
    public void test() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        processor.createSchedule(this).setStartDelayTime(2000).activate();

        //processor.execute(this);

        processor.awaitShutdown();


    }


}
