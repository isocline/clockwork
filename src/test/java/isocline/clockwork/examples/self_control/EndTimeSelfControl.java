package isocline.clockwork.examples.self_control;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndTimeSelfControl implements Work {

    private static Logger logger = Logger.getLogger(EndTimeSelfControl.class.getName());

    private int count = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        count++;

        logger.debug("execute:" + count);

        if(count ==1) {
            event.getWorkSchedule().finishTimeFromNow(Clock.SECOND*2);
        }

        return Clock.SECOND/2;

    }


    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();

        EndTimeSelfControl checker = new EndTimeSelfControl();

        WorkSchedule schedule = processor.newSchedule(checker);
        schedule.subscribe();

        schedule.waitUntilFinish();

        assertEquals(4, checker.count);

        processor.shutdown(TestConfiguration.TIMEOUT);

    }

}
