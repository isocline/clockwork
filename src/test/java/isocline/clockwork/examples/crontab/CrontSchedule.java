package isocline.clockwork.examples.crontab;

import isocline.clockwork.*;
import isocline.clockwork.descriptor.CronDescriptor;
import org.apache.log4j.Logger;
import org.junit.Test;

public class CrontSchedule implements Work {

    private static Logger logger = Logger.getLogger(CrontSchedule.class.getName());

    private int seq = 0;

    public long execute(EventInfo event) throws InterruptedException {

        logger.debug("execute:" + seq++);


        return WAIT;
    }

    @Test
    public void case1() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();


        WorkSchedule schedule = worker.createSchedule(new CronDescriptor("* * * * *"), CrontSchedule.class);

        schedule.activate();

        worker.awaitShutdown();


    }


}
