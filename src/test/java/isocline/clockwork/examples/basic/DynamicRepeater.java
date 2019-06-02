package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class DynamicRepeater implements Work {

    private static Logger logger = Logger.getLogger(DynamicRepeater.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {


        long nexttime = 500 + (long) (Math.random()*1000);

        logger.debug("execute:" + (seq++) + " nexttime:"+nexttime );


        return nexttime;


    }

    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor();



        WorkSchedule schedule = processor.createSchedule(new DynamicRepeater());

        schedule.activate();

        processor.shutdown(TestConfiguration.TIMEOUT);



    }

}
