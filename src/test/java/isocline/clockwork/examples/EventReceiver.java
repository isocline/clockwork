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


        return SLEEP;

    }

    public static void main(String[] args) throws Exception {

        ClockWorker worker = ClockWorkerContext.getWorker();


        EventReceiver checker = new EventReceiver();
        WorkSchedule schedule = worker.createSchedule(checker).setFinishTime(30 * Clock.SECOND).bindEvent("test");
        schedule.activate();


        SignalGenerator gen = new SignalGenerator();
        gen.setEventName("test");

        worker.createSchedule(gen).setFinishTime(30 * Clock.SECOND).setSecondBaseMode(true).activate();


        worker.shutdown(20 * Clock.SECOND);
    }
}
