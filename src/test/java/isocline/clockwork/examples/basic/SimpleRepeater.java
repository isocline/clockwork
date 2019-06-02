package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class SimpleRepeater implements Work {

    private static Logger logger = Logger.getLogger(SimpleRepeater.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("execute:" + seq++);



        return WAIT;
        //return Clock.SECOND;

    }

    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        WorkSchedule schedule = processor.createSchedule(new SimpleRepeater()).setRepeatInterval(1 * Clock.SECOND)
                .setFinishTimeFromNow(5 * Clock.SECOND);

        schedule.activate();

        processor.shutdown(TestConfiguration.TIMEOUT);
        //processor.awaitShutdown();


    }


    @Test
    public void case2() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.createSchedule(SimpleRepeater.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void caseStrictMode() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.createSchedule(SimpleRepeater.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.setStrictMode();
        schedule.activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void delayStart1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.createSchedule(SimpleRepeater.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.setStartDelayTime(Clock.milliseconds(0,0,2));
        schedule.activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }


    @Test
    public void delayStart2() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.createSchedule(SimpleRepeater.class);

        schedule.setRepeatInterval(1 * Clock.SECOND);
        schedule.setStartDateTime(Clock.nextSecond()+Clock.SECOND*2);
        schedule.activate();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }
}
