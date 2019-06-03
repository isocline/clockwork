package isocline.clockwork;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class WorkScheduleTest {

    private static Logger logger = Logger.getLogger(WorkScheduleTest.class.getName());

    private int seq;

    private WorkProcessor processor;

    @Before
    public void before() {
        seq = 0;

        processor = WorkProcessorFactory.getProcessor();
    }

    @After
    public void after() {

        processor.shutdown(1000);
    }

    @Test
    public void executeOneTime() throws Exception {


        processor.newSchedule((WorkEvent event) -> {
            seq++;
            logger.debug("exec " + seq);

            return Work.TERMINATE;
        }).subscribe();

        Thread.sleep(100);


        assertEquals(1, seq);

    }

    @Test
    public void executeSleep() throws Exception {


        processor.newSchedule((WorkEvent event) -> {
            seq++;
            logger.debug("exec " + seq);

            return Work.WAIT;
        }).subscribe();

        Thread.sleep(100);

        assertEquals(1, seq);

    }

    @Test
    public void executeLoop() throws Exception {


        processor.newSchedule((WorkEvent event) -> {
            seq++;
            logger.debug("exec " + seq);

            if (seq == 10) {
                return Work.TERMINATE;
            }

            return Work.LOOP;
        }).subscribe();


        Thread.sleep(100);
        assertEquals(10, seq);

    }


}
