package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class PreciseRepeater implements Work {

    private static Logger logger = Logger.getLogger(PreciseRepeater.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("execute:" + seq++);

        return 10;
    }

    @Test
    public void case1() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();

        WorkSchedule schedule = worker.createSchedule(new PreciseRepeater()).setStrictMode();

        schedule.activate();

        worker.shutdown(TestConfiguration.TIMEOUT);
    }

}
