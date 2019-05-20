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
        logger.debug("invoke - sendSMS start ############################");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        logger.debug("invoke - sendSMS end");


    }

    public void sendMail() {
        logger.debug("invoke - sendMail start");
        try {
            Thread.sleep(2500);
        } catch (Exception e) {

        }
        logger.debug("invoke - sendMail end");

    }


    public void report() {
        logger.debug("");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        logger.debug("************ invoke - report************");

    }


    public void timeout(){
        logger.debug("");
        logger.debug("********** TIME OUT *************");

    }

    public void defineWorkFlow2(WorkFlow flow) {

        flow.next(this::order).run(this::sendMail,"mail").run(this::sendSMS,"x1");

        flow.wait("mail").next(this::report,"x2");

        flow.waitAll("x1","x2","qq").finish();


        flow.wait("error").next(this::report);


    }

    public void defineWorkFlow3(WorkFlow flow) {

        flow.run(this::order).next(this::sendMail,"mail").next(this::sendSMS,"x1");

        flow.wait("mail").next(this::report,"x2");

        flow.waitAll("x1","x2").finish();


    }

    public void defineWorkFlow_XX(WorkFlow flow) {

        flow.run(this::order).next(this::sendMail,"mail").next(this::sendSMS,"x1");

        flow.wait("mail").next(this::report).fireEvent("qq",2000);



        flow.wait("qq").finish();


    }

    public void defineWorkFlow(WorkFlow flow) {

        flow.fireEvent("timeout",3000).run(this::order).next(this::sendMail,"mail").next(this::sendSMS,"x1");

        flow.wait("mail").next(this::sendMail).fireEvent("qq",2000);

        flow.wait("timeout").next(this::timeout).finish();

        flow.waitAll("qq").next(this::report).finish();


    }

    @Test
    public void testSimple() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();


        //processor.createSchedule(this).setStartDelayTime(1000).activate();
        processor.createSchedule(this).activate();


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
