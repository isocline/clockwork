package isocline.clockwork;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class WorkScheduleTest {

    private static Logger logger = Logger.getLogger(WorkScheduleTest.class.getName());

    private int seq;

    private WorkProcessor worker;

    @Before
    public void before() {
        seq = 0;

        worker = WorkProcessorFactory.getDefaultProcessor();
    }

    @After
    public void after() {

        worker.shutdown(1000);
    }

    @Test
    public void executeOneTime() throws Exception {


        worker.createSchedule((WorkEvent event) -> {
            seq++;
            logger.debug("exec " + seq);

            return Work.TERMINATE;
        }).activate();

        Thread.sleep(100);


        assertEquals(1, seq);

    }

    @Test
    public void executeSleep() throws Exception {


        worker.createSchedule((WorkEvent event) -> {
            seq++;
            logger.debug("exec " + seq);

            return Work.WAIT;
        }).activate();

        Thread.sleep(100);

        assertEquals(1, seq);

    }

    @Test
    public void executeLoop() throws Exception {


        worker.createSchedule((WorkEvent event) -> {
            seq++;
            logger.debug("exec " + seq);

            if (seq == 10) {
                return Work.TERMINATE;
            }

            return Work.LOOP;
        }).activate();


        Thread.sleep(100);
        assertEquals(10, seq);

    }


}
