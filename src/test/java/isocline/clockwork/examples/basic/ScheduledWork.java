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

        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        WorkSchedule schedule = processor.newSchedule(new ScheduledWork())
                .interval(1 * Clock.HOUR)
                .startTime("2020-04-24T09:00:00Z")
                .finishTime("2020-06-16T16:00:00Z")
                .subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
        //processor.awaitShutdown();


    }


    @Test
    public void case2() throws Exception {

        WorkProcessor manager = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = manager.newSchedule(ScheduledWork.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.subscribe();


        manager.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void caseStrictMode() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(ScheduledWork.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.setStrictMode();
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void delayStart1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(ScheduledWork.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.startDelayTime(Clock.milliseconds(0,0,2));
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }


    @Test
    public void delayStart2() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(ScheduledWork.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.startTime(Clock.nextSecond()+Clock.SECOND*2);
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }
}
