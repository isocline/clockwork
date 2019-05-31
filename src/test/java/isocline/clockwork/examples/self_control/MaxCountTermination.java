package isocline.clockwork.examples.self_control;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxCountTermination implements Work {

    private static Logger logger = Logger.getLogger(MaxCountTermination.class.getName());

    private int count = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        count++;

        logger.debug("execute:" + count);

        if(count==3) {
            return TERMINATE;
        }

        return Clock.SECOND;

    }




    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        MaxCountTermination checker = new MaxCountTermination();

        WorkSchedule schedule = processor.createSchedule(checker);
        schedule.activate();

        //wait until finish
        schedule.waitUntilFinish();

        assertEquals(3, checker.count);


        processor.shutdown(TestConfiguration.TIMEOUT);

    }

}
