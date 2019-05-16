package isocline.clockwork.examples.object;

import isocline.clockwork.*;
import isocline.clockwork.object.FunctionExecutor;
import org.apache.log4j.Logger;

public class OrderProcess3 implements Work {

    private static Logger logger = Logger.getLogger(OrderProcess3.class.getName());


    private String id;

    public OrderProcess3(String id) {
        this.id = id;
    }


    private void writeLog() {

        logger.debug(id + " writeLog");

    }


    private void record() {
        logger.debug(id + " record");
    }


    public void checkStock() {
        logger.debug(id + " checkStock");

        try {
            Thread.sleep(2000);
        }catch (Exception e) {

        }

        logger.debug(id + " checkStock end");

    }


    private void checkSupplier() {

        logger.debug(id + " checkSupplier");

        try {
            Thread.sleep(3000);
        }catch (Exception e) {

        }

        logger.debug(id + " checkSupplier end");

    }


    private void checkUserPoint() {
        logger.debug(id + " checkUserPoint");

    }


    private void makeMessage() {
        logger.debug(id + " makeMessage");
    }


    private void recordUserInfo() {
        logger.debug(id + " recordUserInfo");
    }


    private ProcessFlow processFlow;

    public void processFlow(ProcessFlow flow) {

        flow
                //.run(this::writeLog)
                .run(this::record)
                .runAsync(this::checkStock, "checkStock")
                .runAsync(this::checkSupplier, "checkSup");

        flow
                .runWait(this::makeMessage, "checkStock&checkSup").end();

        flow
                .runWait(this::recordUserInfo, "error");

//        flow.run((ki)->{System.out.println()}));


    }

    ProcessFlow flow = new ProcessFlow();


    WorkSchedule schedule;

    public void regist(WorkProcessor worker) {

        logger.debug("regist");
        processFlow(flow);



        schedule = worker.createSchedule(this);

        for (FunctionExecutor waiter : flow.getWaiters()) {
            String eventName = waiter.getRecvEventName();

            logger.debug("event bind "+eventName);
            //schedule.bindEvent(eventName);

        }


        //schedule.bindEvent("async");

    }

    public void execute(WorkProcessor worker) {



        regist(worker);

        logger.debug("start exec");
        //this.schedule.setStartTime(1).activate();
        this.schedule.activate();


    }



    public static void main(String[] args) throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getProcessor("perform", Configuration.PERFORMANCE);


        OrderProcess3 p = new OrderProcess3("AutoExpress");




        p.execute(worker);



        worker.shutdown(1000000);

    }


    @Override
    public long execute(EventInfo event) throws InterruptedException {

        //logger.debug("---- exec ---");


        String eventName = event.getEventName();
        FunctionExecutor exec = null;

        if(eventName!=null) {
            exec = flow.getExecutor(eventName);

            if (exec != null) {

                exec.execute();

                if (exec.isLastExecutor()) {
                    return TERMINATE;
                } else {
                    return WAIT;
                }


            }
        }
        exec = (FunctionExecutor) event.getAttribute("exec.func");
        if(exec!=null) {
            event.removeAttribute("exec.func");
            exec.execute();

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {

                logger.debug("fire event "+eventNm);
                //event.getWorkSchedule().getWorkProcessor().raiseEvent(eventNm, event);
                event.getWorkSchedule().raiseLocalEvent(event.setEventName(eventNm));
            }
            if(exec.isLastExecutor()) {
                return TERMINATE;
            }else {
                return WAIT;
            }
        }


        exec = flow.getNextExecutor();
        if (exec != null) {

            if(exec.isAsync()) {
                //logger.debug("ASYNC");
                EventInfo newEvent = new EventInfo();
                newEvent.setAttribute("exec.func",exec);
                //worker.createSchedule(this).bindEvent("async").activate();

                //event.getWorkSchedule().getWorkProcessor().raiseEvent("async", newEvent);

                event.getWorkSchedule().raiseLocalEvent(newEvent);

                //logger.debug("ASYNC - END");
                return 1;
            }


            exec.execute();

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {

                //event.getWorkSchedule().getWorkProcessor().raiseEvent(eventNm, event);


                event.getWorkSchedule().raiseLocalEvent(event.setEventName(eventNm));
            }

            if(exec.isLastExecutor()) {
                return TERMINATE;
            }

        }else {
            return WAIT;
        }
        return 1;
    }


}
