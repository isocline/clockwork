package isocline.clockwork.examples.object;

import isocline.clockwork.Configuration;
import isocline.clockwork.ProcessFlow;
import isocline.clockwork.WorkProcessor;
import isocline.clockwork.WorkProcessorFactory;
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

    public String test(){
        logger.debug(id+ "start test");

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


    public void processFlow(ProcessFlow flow) {
        String z="1234";

        flow
                .run(this::writeLog)


                .run(this::record)

                .runAsync(this::checkStock, "checkStock")
                .runAsync(this::checkSupplier, "checkSup")


                .runWait(this::makeMessage, "checkStock&checkSup").run(this::test).end();

        flow
                .runWait(this::processError, "error").end();


    }


    public static void main(String[] args) throws Exception {

        WorkProcessor worker = WorkProcessorFactory.getProcessor("perform", Configuration.PERFORMANCE);


        OrderProcess2 p = new OrderProcess2("AutoExpress2");


        p.execute(worker);


        worker.shutdown(1000000);

    }


}
