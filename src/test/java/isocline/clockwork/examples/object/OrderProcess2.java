package isocline.clockwork.examples.object;

import isocline.clockwork.ClockWorker;
import isocline.clockwork.Configuration;
import isocline.clockwork.ProcessFlow;
import isocline.clockwork.object.WorkObject;
import org.apache.log4j.Logger;

public class OrderProcess2 implements WorkObject {

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


    public void processFlow(ProcessFlow flow) {

        flow
                .run(this::writeLog)
                .run(this::record)
                .runAsync(this::checkStock, "checkStock")
                .runAsync(this::checkSupplier, "checkSup");

        flow
                .runWait(this::makeMessage, "checkStock&checkSup").end();

        flow
                .runWait(this::recordUserInfo, "error");


    }


    public static void main(String[] args) throws Exception {

        ClockWorker worker = new ClockWorker("perform", Configuration.PERFORMANCE);


        OrderProcess2 p = new OrderProcess2("AutoExpress2");


        p.execute(worker);


        worker.shutdown(1000000);

    }


}
