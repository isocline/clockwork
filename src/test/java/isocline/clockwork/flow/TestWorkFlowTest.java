package isocline.clockwork.flow;

import isocline.clockwork.FlowableWork;
import isocline.clockwork.WorkFlow;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.WorkProcessorFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestWorkFlowTest implements FlowableWork {

    private boolean chk = false;


    private static Logger logger = Logger.getLogger(TestWorkFlowTest.class.getName());


    public void checkMemory() {
        log("check MEMORY");
    }

    public void checkStorage() {
        log("check STORAGE");
    }

    public void sendSignal() {
        log("send SIGNAL");
    }

    public void sendStatusMsg() {
        log("send STATUS MSG");
    }

    public void sendReportMsg() {
        log("send REPORT MSG");
    }

    public void report() {
        log("REPORT");
        chk = true;

    }


    public void defineWorkFlow(WorkFlow flow) {

        WorkFlow p1 = flow.next(this::checkMemory).next(this::checkStorage);

        flow.run(this::sendSignal);

        WorkFlow t2 = flow.wait(p1).next(this::sendStatusMsg).next(this::sendReportMsg);

        flow.waitAll( t2).next(this::report).finish();
    }


    @Test
    public void testStartByEvent() {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        processor.createSchedule(this).activate();

        processor.awaitShutdown();

        assertEquals(true, chk);

    }

    private void log(String msg) {
        log(msg, -1);
    }

    private void log(String msg, long delayTime) {
        logger.debug(msg + " #START");

        if (delayTime == -1) {
            delayTime = 500 + (long) (3000 * Math.random());
        }

        if (delayTime > 0) {

            try {
                logger.debug(msg + " #WAIT - " + delayTime);
                Thread.sleep(delayTime);
            } catch (Exception e) {

            }
        }

        logger.debug(msg + " #END\n");
    }


}
