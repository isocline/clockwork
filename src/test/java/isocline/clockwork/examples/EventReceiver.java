package isocline.clockwork.examples;

import isocline.clockwork.*;
import isocline.clockwork.module.WorkEventGenerator;
import org.apache.log4j.Logger;


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

    public static void main(String[] args) throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        EventReceiver checker = new EventReceiver();
        WorkSchedule schedule = processor.newSchedule(checker).finishTimeFromNow(30 * Clock.SECOND).bindEvent("test").setSleepMode();
        schedule.subscribe();


        WorkEventGenerator gen = new WorkEventGenerator();
        gen.setEventName("test");

        long startTime = Clock.nextSecond(900);



        processor.newSchedule(gen).finishTimeFromNow(30 * Clock.SECOND).setStrictMode().startTime
                (startTime).subscribe();


        processor.shutdown(20 * Clock.SECOND);
    }
}
