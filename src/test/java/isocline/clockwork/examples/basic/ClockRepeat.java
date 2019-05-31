package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.descriptor.CronDescriptor;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ClockRepeat implements Work {

    private static Logger logger = Logger.getLogger(ClockRepeat.class.getName());

    public long execute(WorkEvent event) throws InterruptedException {
        logger.debug("13");

        return 24 * Clock.HOUR;

    }

    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();
        processor.createSchedule(new CronDescriptor("49 1 * * *"), this).activate();

        processor.shutdown(3000);
        //processor.awaitShutdown();
    }


}
