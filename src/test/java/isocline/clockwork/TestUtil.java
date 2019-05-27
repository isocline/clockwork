package isocline.clockwork;

public final class TestUtil {


    public final static void waiting(long waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException ie) {

        }
    }
}
