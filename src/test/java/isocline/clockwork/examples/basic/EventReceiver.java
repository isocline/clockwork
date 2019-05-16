package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import isocline.clockwork.module.SignalGenerator;
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

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();


        WorkSchedule schedule = processor.createSchedule(new EventReceiver()).bindEvent("example-event");
        schedule.activate();


        SignalGenerator gen = new SignalGenerator();
        gen.setEventName("example-event");


        processor.createSchedule(gen).setFinishTimeFromNow(30 * Clock.SECOND).setStrictMode().setStartDateTime
                (Clock.nextSecond()).activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }


    @Test
    public void simpleStyle() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();




        processor.regist(new EventReceiver(), "example-event");




        SignalGenerator gen = new SignalGenerator();
        gen.setEventName("example-event");

        processor.createSchedule(gen).setFinishTimeFromNow(30 * Clock.SECOND).setStrictMode().setStartDateTime
                (Clock.nextSecond()).activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }
}
