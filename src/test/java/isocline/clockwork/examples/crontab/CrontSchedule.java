package isocline.clockwork.examples.crontab;

import isocline.clockwork.*;
import isocline.clockwork.descriptor.CronDescriptor;
import org.apache.log4j.Logger;
import org.junit.Test;

public class CrontSchedule implements Work {

    private static Logger logger = Logger.getLogger(CrontSchedule.class.getName());

    private int count = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("activate:" + count++);


        if(count==2) return TERMINATE;

        return WAIT;
    }

    @Test
    public void case1() throws Exception {


        WorkProcessor.main()
                .newPlan(CrontSchedule.class)
                .scheduleDescriptor(new CronDescriptor("* * * * *"))
                .activate();

        WorkProcessor.main().shutdown(10*Clock.SECOND);


    }

    @Test
    public void case2() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();


        Plan schedule = processor.newPlan(new CronDescriptor("* * * * *"), CrontSchedule.class);

        schedule.activate();

        processor.shutdown(Clock.MINUTE);


    }


}
