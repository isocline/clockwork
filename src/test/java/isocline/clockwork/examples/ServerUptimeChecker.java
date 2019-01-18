package isocline.clockwork.examples;

import isocline.clockwork.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerUptimeChecker  implements Work {

    private static Logger logger = Logger.getLogger(ServerUptimeChecker.class.getName());


    private URL url;

    public ServerUptimeChecker(String strUrl) throws MalformedURLException {

        this.url = new URL(strUrl);
    }

    public long execute(EventInfo event) throws InterruptedException {


        try {
            long t1 = System.currentTimeMillis();

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            urlConn.disconnect();

            long t2 = System.currentTimeMillis();

            long timeGap = t2-t1;

            logger.debug(this.url.toString() +" OK. connection time="+timeGap);

            if(timeGap<100) {
                return 2*Clock.SECOND;
            }else if(timeGap>=10 && timeGap<1000) {
                return 5*Clock.SECOND;
            }else {
                return 10*Clock.SECOND;
            }

        }catch (IOException ioe) {

            return 30*Clock.SECOND;

        }

   }

   public static void main(String[] args) throws Exception {
       ClockWorker worker = ClockWorkerContext.getWorker();


       String[] urls = new String[] {"https://www.google.com","https://www.apple.com"};
       for(String url:urls) {

           ServerUptimeChecker checker = new ServerUptimeChecker( url);
           WorkSchedule schedule = worker.createSchedule(checker).bindEvent("connectTypeChange");
           schedule.start();
       }



       worker.shutdown(20*Clock.SECOND);
   }
}
