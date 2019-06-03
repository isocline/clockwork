package isocline.clockwork.examples;

import isocline.clockwork.*;
import org.apache.log4j.Logger;


/**
 * https://www-01.ibm.com/support/docview.wss?uid=swg21089949
 */
public class MailSender implements Work {

    private static Logger logger = Logger.getLogger(MailSender.class.getName());

    long timeUnit = Clock.SECOND;

    private String email;
    private String title;
    private String content;

    private int failCount = 0;

    public MailSender(String email,String title, String content)  {
        this.email = email;
        this.title = title;
        this.content = content;

    }

    private boolean send( ) {

        return false;
    }

    public long execute(WorkEvent event) throws InterruptedException {




        if(send()) {
            return TERMINATE;
        }else {
            logger.error("send fail");
            failCount++;

            if(failCount>20) {
                logger.error("max fail count");
                return TERMINATE;
            }

            long timeGap = failCount * 15 * timeUnit;

            if(timeGap>45*timeUnit) {
                timeGap = 45 * timeUnit;
            }



            return timeGap;
        }

   }

   public static void main(String[] args) throws Exception {
       WorkProcessor processor = WorkProcessorFactory.getProcessor();


       String[] emails = new String[] {"test@test.com","test2@test.com"};
       for(String email:emails) {

           MailSender checker = new MailSender( email, "Test", "test");
           WorkSchedule schedule = processor.newSchedule(checker);
           schedule.subscribe();
       }



       processor.shutdown(20*Clock.SECOND);
   }
}
