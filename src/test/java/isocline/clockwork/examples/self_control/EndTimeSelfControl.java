package isocline.clockwork.examples.self_control;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class EndTimeSelfControl implements Work {

    private static Logger logger = Logger.getLogger(EndTimeSelfControl.class.getName());

    private int seq = 0;

    public long execute(EventInfo event) throws InterruptedException {

        seq++;

        logger.debug("execute:" + seq);

        if(seq==1) {
            event.getWorkSchedule().setFinishTimeFromNow(Clock.SECOND*3);
        }

        return Clock.SECOND/10;

    }


    @Test
    public void case1() throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getDefaultProcessor();




        EndTimeSelfControl checker = new EndTimeSelfControl();
        WorkSchedule schedule = worker.createSchedule(checker);

        schedule.activate();

        worker.shutdown(TestConfiguration.TIMEOUT);

    }

}
