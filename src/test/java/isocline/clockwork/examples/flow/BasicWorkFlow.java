package isocline.clockwork.examples.flow;

import isocline.clockwork.ProcessFlow;
import isocline.clockwork.Work;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.WorkProcessorFactory;
import org.apache.log4j.Logger;
import org.junit.Test;


public class BasicWorkFlow implements Work {


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

    public void processFlow(ProcessFlow flow) {

        flow.run(this::order);
        flow.runAsync(this::sendSMS).runAsync(this::sendMail, "chk");
        flow.runWait(this::report, "chk");
        flow.end();

    }


    @Test
    public void test() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        processor.execute(this);

        processor.awaitShutdown();


    }


}
