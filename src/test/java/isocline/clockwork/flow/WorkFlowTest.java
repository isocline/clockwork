package isocline.clockwork.flow;

import isocline.clockwork.*;
import isocline.clockwork.module.WorkEventGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;


public class WorkFlowTest implements FlowableWork {


    private static Logger logger = Logger.getLogger(WorkFlowTest.class.getName());


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
    public void testSimple() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();


        processor.createSchedule(this).setStartDelayTime(1000).activate();


        processor.awaitShutdown();


    }

    @Test
    public void testStartByEvent() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();


        processor.createSchedule(this).bindEvent("start").activate();


        WorkEventGenerator gen = new WorkEventGenerator();
        gen.setEventName("start");
        gen.setRepeatTime(Work.TERMINATE);

        processor.createSchedule(gen).setStartDelayTime(1000).activate();

        processor.awaitShutdown();


    }


}
