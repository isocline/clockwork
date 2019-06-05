package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MultiAsync2 {


    private static Logger logger = Logger.getLogger(MultiAsync2.class.getName());

    private List<Long> list = Collections.synchronizedList(new ArrayList());


    public void asyncMulti() {
        logger.debug("** invoke - asyncMulti");
        long calc = 100 + (long) (1000 * Math.random());
        TestUtil.waiting(calc);
        list.add(calc);
        logger.debug("** invoke - async1 - END " + calc);
    }


    public void sum(WorkEvent event) {
        long sum = list.stream().mapToLong(x -> x).sum();
        logger.debug("** invoke - sum - result:" + sum);
    }


    @Test
    public void test() throws InterruptedException {

        WorkProcessor.main().reflow(
                flow -> {
                    flow.runAsync(this::asyncMulti, 5)
                            .waitAll()
                            .next(this::sum);
                }

        ).startDelayTime(2000).run();


    }


}
