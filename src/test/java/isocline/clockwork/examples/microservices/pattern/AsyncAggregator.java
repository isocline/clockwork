package isocline.clockwork.examples.microservices.pattern;

import isocline.clockwork.*;
import isocline.clockwork.log.XLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncAggregator implements FlowableWork {


    private XLogger logger = XLogger.getLogger(AsyncAggregator.class);

    public void init() {
        logger.debug("init");
    }

    public void callService1(WorkEvent e) {
        logger.debug("Service1 - start");
        TestUtil.waiting(1500);
        logger.debug("Service1 - end");

        e.root().setAttribute("result:service1", "A");
    }



    public void callService2(WorkEvent e) {
        logger.debug("Service2 - start");
        TestUtil.waiting(1200);
        logger.debug("Service2 - end");
        e.root().setAttribute("result:service2", "B");
    }

    public void callService3(WorkEvent e) {
        logger.debug("Service3 - start");
        TestUtil.waiting(1000);
        logger.debug("Service3 - end");
        e.root().setAttribute("result:service3", "C");
    }

    public void finish(WorkEvent e) {

        String result = e.root().getAttribute("result:service1").toString()
                +  e.root().getAttribute("result:service2")
                +  e.root().getAttribute("result:service3");

        assertEquals("ABC", result);

        logger.debug("finish - "+result);
    }


    public void onTimeout(WorkEvent e) {
        logger.debug("timeout  "+e.getEventName());
    }

    public void onError(WorkEvent e) {

        logger.debug("error "+e.getEventName());

        Throwable err = e.getThrowable();
        if(err!=null) {
            err.printStackTrace();
        }
    }

    @Override
    public void defineWorkFlow(WorkFlow flow) {
        WorkFlow s1 = flow.next(this::init).fireEvent("timeout",3000);

        WorkFlow p1 = flow.wait(s1).next(this::callService1);
        WorkFlow p2 = flow.wait(s1).next(this::callService2);
        WorkFlow p3 = flow.wait(s1).next(this::callService3);

        flow.waitAll(p1,p2,p3).next(this::finish).finish();


        flow.onError("*").next(this::onError).finish();
        flow.wait("timeout").next(this::onTimeout).finish();

    }


    @Test
    public void startTest() {
        WorkProcessor processor = WorkProcessorFactory.getProcessor();

        processor.execute(new AsyncAggregator());

        processor.awaitShutdown();
    }
}
