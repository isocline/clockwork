package isocline.clockwork.examples;

import isocline.clockwork.*;
import isocline.clockwork.module.SignalGenerator;
import org.apache.log4j.Logger;


/**
 * https://www-01.ibm.com/support/docview.wss?uid=swg21089949
 */
public class EventReceiver implements Work {

    private static Logger logger = Logger.getLogger(EventReceiver.class.getName());


    private int failCount = 0;

    public EventReceiver() {

    }


    public long execute(EventInfo event) throws InterruptedException {

        logger.debug("receive:" + event.getEventName());


        return WAIT;

    }

    public static void main(String[] args) throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();


        EventReceiver checker = new EventReceiver();
        WorkSchedule schedule = worker.createSchedule(checker).setFinishTimeFromNow(30 * Clock.SECOND).bindEvent("test").setSleepMode();
        schedule.activate();


        SignalGenerator gen = new SignalGenerator();
        gen.setEventName("test");

        long startTime = Clock.nextSecond(900);



        worker.createSchedule(gen).setFinishTimeFromNow(30 * Clock.SECOND).setStrictMode().setStartDateTime
                (startTime).activate();


        worker.shutdown(20 * Clock.SECOND);
    }
}
