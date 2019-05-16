package isocline.clockwork.examples.flow;

import isocline.clockwork.*;
import org.apache.log4j.Logger;

public class OrderProcess3 implements FlowableWork {

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
        } catch (Exception e) {

        }

        logger.debug(id + " checkStock end");

    }


    private void checkSupplier() {

        logger.debug(id + " checkSupplier");

        try {
            Thread.sleep(3000);
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


    public void defineWorkFlow(WorkFlow flow) {

        flow
                //.run(this::writeLog)
                .run(this::record)
                .next(this::checkStock, "checkStock")
                .next(this::checkSupplier, "checkSup");

        flow
                .wait("checkStock&checkSup").next(this::makeMessage).finish();


    }

    public static void main(String[] args) throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getProcessor("perform", Configuration.PERFORMANCE);


        OrderProcess3 p = new OrderProcess3("AutoExpress");

        worker.execute(p);


        worker.shutdown(1000000);

    }


}
