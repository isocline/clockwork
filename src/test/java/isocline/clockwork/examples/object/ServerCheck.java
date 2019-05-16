package isocline.clockwork.examples.object;

import isocline.clockwork.Clock;
import isocline.clockwork.WorkProcessorFactory;
import isocline.clockwork.object.LiveObject;

public class ServerCheck extends LiveObject {

    private int count = 0;

    public long check() {

        count++;

        if(count>10) {
            return TERMINATE;
        }
        System.err.println("check");
        return Clock.SECOND;
    }


    public static void main(String[] args) throws  Exception {

        ServerCheck chk = new ServerCheck();
        chk.start();

        System.out.println("zzz");

        WorkProcessorFactory.getDefaultProcessor().awaitShutdown();

    }


}
