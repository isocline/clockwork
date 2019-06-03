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


        WorkSchedule schedule = processor.newSchedule(new SimpleRepeater())
                .interval(1 * Clock.SECOND)
                .finishTimeFromNow(5 * Clock.SECOND);

        schedule.subscribe();

        processor.shutdown(TestConfiguration.TIMEOUT);
        //processor.awaitShutdown();


    }


    @Test
    public void case2() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(SimpleRepeater.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void caseStrictMode() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(SimpleRepeater.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.setStrictMode();
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }

    @Test
    public void delayStart1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(SimpleRepeater.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.startDelayTime(Clock.milliseconds(0,0,2));
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }


    @Test
    public void delayStart2() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.newSchedule(SimpleRepeater.class);

        schedule.interval(1 * Clock.SECOND);
        schedule.startTime(Clock.nextSecond()+Clock.SECOND*2);
        schedule.subscribe();


        processor.shutdown(TestConfiguration.TIMEOUT);
    }
}
