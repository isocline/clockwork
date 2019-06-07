package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import isocline.clockwork.module.WorkEventGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * https://www-01.ibm.com/support/docview.wss?uid=swg21089949
 */
public class EventReceiver implements Work {

    private static Logger logger = Logger.getLogger(EventReceiver.class.getName());


    private int failCount = 0;

    public EventReceiver() {

    }


    public long execute(WorkEvent event) throws InterruptedException {


        logger.debug("receive:" + event.getEventName());


        return WAIT;

    }

    @Test
    public void basicStyle() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        Plan schedule = processor.newPlan(new EventReceiver()).bindEvent("example-event");
        schedule.activate();


        WorkEventGenerator gen = new WorkEventGenerator();
        gen.setEventName("example-event");


        processor.newPlan(gen)

                .setStrictMode()
                .startTime(Clock.nextSecond())
                .finishTimeFromNow(30 * Clock.SECOND)
                .activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }


    @Test
    public void simpleStyle() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();




        processor.newPlan(new EventReceiver(), "example-event").activate();




        WorkEventGenerator gen = new WorkEventGenerator();
        gen.setEventName("example-event");

        processor.newPlan(gen).finishTimeFromNow(30 * Clock.SECOND).setStrictMode().startTime
                (Clock.nextSecond()).activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }
}
