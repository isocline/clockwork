package isocline.clockwork.examples.flow;

import isocline.clockwork.ProcessFlow;
import isocline.clockwork.Work;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.WorkProcessorFactory;
import org.apache.log4j.Logger;
import org.junit.Test;


public class ComplexWorkFlow implements Work {


    private static Logger logger = Logger.getLogger(ComplexWorkFlow.class.getName());

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

        flow.run(wrwer).next(wewerwr,"multi1");
        flow.run(werwer).next(werwer,"multi2");

        flow.wait("multi1 & mult2").run("123").end

        flow.wait("b&a").then(test);


        flow.wait("1?&23").end()


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
