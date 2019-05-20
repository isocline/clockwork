package isocline.clockwork.examples.self_control;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MaxCountTermination implements Work {

    private static Logger logger = Logger.getLogger(MaxCountTermination.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        seq++;

        logger.debug("execute:" + seq);

        if(seq==3) {
            return TERMINATE;
        }

        return Clock.SECOND;

    }


    @Test
    public void case1() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();




        MaxCountTermination checker = new MaxCountTermination();
        WorkSchedule schedule = worker.createSchedule(checker);

        schedule.activate();

        worker.shutdown(TestConfiguration.TIMEOUT);

    }

}
