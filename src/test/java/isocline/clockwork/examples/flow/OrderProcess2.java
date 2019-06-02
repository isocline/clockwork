package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;

public class OrderProcess2 implements FlowableWork {

    private static Logger logger = Logger.getLogger(OrderProcess2.class.getName());


    private String id;

    public OrderProcess2(String id) {
        this.id = id;
    }


    private void writeLog() {

        logger.debug(id + " writeLog");

    }


    private void record() {
        logger.debug(id + " record");
    }

    public String test() {
        logger.debug(id + "start test");

        //if(1>0) throw new RuntimeException("zzzz");
        return "x";
    }


    public void checkStock() {
        logger.debug(id + " checkStock");

        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }

        logger.debug(id + " checkStock end");

    }


    private void checkSupplier() {

        logger.debug(id + " checkSupplier");

        try {
            Thread.sleep(3500);
        } catch (Exception e) {

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


    public void processError() {
        System.err.println("ERRR --");
    }


    public void defineWorkFlow(WorkFlow flow) {
        String z = "1234";

        flow
                .runAsync(this::writeLog)


                .next(this::record)

                .runAsync(this::checkStock, "checkStock")
                .runAsync(this::checkSupplier, "checkSup")


                .wait("checkStock&checkSup").next(this::makeMessage).next(this::test).finish();


    }


    public static void main(String[] args) throws Exception {

        WorkProcessor processor = WorkProcessorFactory.getProcessor("perform", Configuration.PERFORMANCE);


        OrderProcess2 p = new OrderProcess2("AutoExpress2");

        processor.execute(p);


        processor.shutdown(1000000);

    }


}
