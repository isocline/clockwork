package isocline.clockwork.examples;

import isocline.clockwork.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpComm implements Work {

    private URL url;
    HttpURLConnection urlConn;

    public HttpComm(String strUrl) throws MalformedURLException {
        this.url = new URL(strUrl);
    }

    private void connet() throws IOException {
        urlConn = (HttpURLConnection) url.openConnection();

    }

    private void read() throws IOException {

        InputStream is = urlConn.getInputStream();


        BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );

        int countSize = 0;
        String line = null;
        while( ( line = reader.readLine() ) != null )  {
            //System.out.println(line);
            if(line!=null) {
                countSize = countSize+line.length();
            }
        }

        reader.close();

        urlConn.disconnect();

        System.out.println(" "+countSize);

    }
    private int seq=0;

    @Override
    public long execute(WorkEvent event) throws InterruptedException {
        seq++;
        try {
            if (seq%2== 1) {
                connet();
                return 500;
            }else {
                read();

                if(seq>50)
                    return TERMINATE;
                return Work.LOOP;
            }
        }catch (IOException ioe) {

        }
        return TERMINATE;
    }

    public static void main(String[] args) throws Exception {
        WorkProcessor processor = WorkProcessorFactory.getDefaultProcessor();

        long startTime = Clock.nextSecond();

        for(int i=0;i<20;i++) {
            HttpComm work = new HttpComm("https://www.google.com");
            //processor.createSchedule(work).setStartTime(startTime+i*50).activate();
            processor.createSchedule(work).setStrictMode().activate();

        }
        processor.awaitShutdown();
    }
}
