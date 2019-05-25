package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class TestFlow implements FlowableWork {


    org.apache.log4j.Logger logger = Logger.getLogger(TestFlow.class);

    public void step1() {
        count = 0;
        logger.debug("step1");
    }

    public void step2(WorkEvent event) {
        logger.debug("step2");


        for (int i = 0; i < 10; i++) {
            logger.debug("step2--");
            event.getWorkSchedule().raiseLocalEvent(event.createChild("chk"), 1000);
        }

    }

    private int count = 0;

    public void step3(WorkEvent event) {

        count++;
        if (count == 10) {
            logger.debug("FIRE============");
            event.getWorkSchedule().raiseLocalEvent(event.createChild("finish"));
        }

        int s = 123;
        for(int i=0;i<200;i++) {
            for(int j=0;j<1000l;j++) {
                s = i+j+s;
            }
        }
        logger.debug("step3 "+s);
    }

    public synchronized void waitEnjob() throws InterruptedException {
        logger.debug("wait");
        this.wait();
        logger.debug("FINISH");
    }

    private static int cnt = 0;
    private  static AtomicInteger integer = new AtomicInteger(0);
    public synchronized void finish() {

        cnt++;
        integer.addAndGet(1);

        logger.debug("finish "+cnt+ " " +integer.get());
        this.notifyAll();

    }

    @Override
    public void defineWorkFlow(WorkFlow flow) {
        flow.next(this::step1).next(this::step2);

        flow.wait("chk").next(this::step3);

        flow.wait("finish").next(this::finish).finish();

    }

    public static void main(String[] arg) throws Exception {
        WorkProcessor processor = WorkProcessorFactory.getProcessor("perform", Configuration.NOMAL);

        for (int i = 0; i < 10; i++) {

            TestFlow tf = new TestFlow();
            processor.execute(tf);
        }


        processor.awaitShutdown();
    }
}
