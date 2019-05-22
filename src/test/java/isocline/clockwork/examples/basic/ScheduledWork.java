package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ScheduledWork implements Work {

    private static Logger logger = Logger.getLogger(ScheduledWork.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("execute:" + seq++);


        return WAIT;
    }

    @Test
    public void case1() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();


        WorkSchedule schedule = worker.createSchedule(new ScheduledWork())
                .setRepeatInterval(1 * Clock.HOUR)
                .setStartDateTime("2020-04-24T09:00:00Z")
                .setFinishDateTime("2020-06-16T16:00:00Z")
                .activate();


        worker.shutdown(TestConfiguration.TIMEOUT);
        //worker.awaitShutdown();


    }


    @Test
    public void case2() throws Exception {

        WorkProcessor manager = WorkProcessorFactory.getDefaultProcessor();



        WorkSchedule schedule = manager.createSchedule(ScheduledWork.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.activate();


        manager.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void caseStrictMode() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();



        WorkSchedule schedule = worker.createSchedule(ScheduledWork.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.setStrictMode();
        schedule.activate();


        worker.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void delayStart1() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();



        WorkSchedule schedule = worker.createSchedule(ScheduledWork.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.setStartDelayTime(Clock.fromNow(0,0,2));
        schedule.activate();


        worker.shutdown(TestConfiguration.TIMEOUT);
    }


    @Test
    public void delayStart2() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();



        WorkSchedule schedule = worker.createSchedule(ScheduledWork.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.setStartDateTime(Clock.nextSecond()+Clock.SECOND*2);
        schedule.activate();


        worker.shutdown(TestConfiguration.TIMEOUT);
    }
}
