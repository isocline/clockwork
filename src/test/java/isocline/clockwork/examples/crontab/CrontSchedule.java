package isocline.clockwork.examples.crontab;

import isocline.clockwork.*;
import isocline.clockwork.descriptor.CronDescriptor;
import org.apache.log4j.Logger;
import org.junit.Test;

public class CrontSchedule implements Work {

    private static Logger logger = Logger.getLogger(CrontSchedule.class.getName());

    private int count = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("execute:" + count++);


        if(count==2) return TERMINATE;

        return WAIT;
    }

    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();


        WorkSchedule schedule = processor.createSchedule(new CronDescriptor("* * * * *"), CrontSchedule.class);

        schedule.activate();

        processor.awaitShutdown();


    }


}
