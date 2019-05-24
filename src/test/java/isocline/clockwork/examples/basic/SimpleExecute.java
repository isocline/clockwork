package isocline.clockwork.examples.basic;

import isocline.clockwork.*;
import isocline.clockwork.examples.TestConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

public class SimpleExecute implements Work {

    private static Logger logger = Logger.getLogger(SimpleExecute.class.getName());

    private int seq = 0;

    public long execute(WorkEvent event) throws InterruptedException {

        logger.debug("execute:" + seq++);


        return TERMINATE;

    }

    @Test
    public void case1() throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();



        // execute async
        processor.execute(new SimpleExecute());


        processor.shutdown(TestConfiguration.TIMEOUT);

    }


}
