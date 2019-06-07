package isocline.clockwork;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class WorkTest  {

    private static Logger logger = Logger.getLogger(WorkTest.class.getName());

    private int seq;

    private WorkProcessor workProcessor;

    @Before
    public void before() {
        seq=0;

        workProcessor = WorkProcessorFactory.getProcessor();
    }

    @After
    public void after() {

        workProcessor.shutdown(1000);
    }

    @Test
    public void executeSimple() throws Exception {


        Plan schedule =

        workProcessor.execute((WorkEvent event) -> {
            seq++;
            logger.debug("exec " +seq);

            return Work.TERMINATE;
        });

        schedule.block(1000);


        assertEquals(1, seq);

    }

    @Test
    public void executeByEvent() throws Exception {

        Plan schedule =

        workProcessor.newPlan((WorkEvent event) -> {
            seq++;
            logger.debug("exec " +seq + " event:"+event.getEventName());

            return Work.WAIT;
        }, "testEvent").activate();

        workProcessor.execute((WorkEvent event) -> {
            logger.debug("fire event:"+event.getEventName());

            event.getPlan().getWorkProcessor().raiseEvent("testEvent", event);

            return Work.TERMINATE;
        });

        schedule.block(1000);


        assertEquals(1, seq);

    }


    @Test
    public void executeOneTime() throws Exception {


        Plan schedule =

        workProcessor.newPlan((WorkEvent event) -> {
            seq++;
            logger.debug("exec " +seq);

            return Work.TERMINATE;
        }).activate();

        schedule.block(1000);


        assertEquals(1, seq);

    }

    @Test
    public void executeSleep() throws Exception{

        Plan schedule =

        workProcessor.newPlan((WorkEvent event) -> {
            seq++;
            logger.debug("exec " +seq);

            return Work.WAIT;
        }).activate();

        schedule.block(100);

        assertEquals(1, seq);

    }

    @Test
    public void executeLoop() throws Exception{

        Plan schedule =

        workProcessor.newPlan((WorkEvent event) -> {
            seq++;
            logger.debug("exec " +seq);

            if(seq==10) {
                return Work.TERMINATE;
            }

            return Work.LOOP;
        }).activate();


        schedule.block(100);
        assertEquals(10, seq);

    }


}
