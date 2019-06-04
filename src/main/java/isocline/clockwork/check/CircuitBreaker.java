package isocline.clockwork.check;

import isocline.clockwork.Clock;
import isocline.clockwork.WorkEvent;

import java.util.HashMap;
import java.util.Map;

public class CircuitBreaker {

    private String id;
    private int failCount;

    private long lastFailTime = 0;

    private int maxFailCount = 3;

    private long retryTimeGap = Clock.SECOND * 10;


    private static Map<String, CircuitBreaker> map = new HashMap<>();

    public static CircuitBreaker create(String id) {

        CircuitBreaker circuitBreaker = map.get(id);

        if (circuitBreaker == null) {
            circuitBreaker = new CircuitBreaker(id);
            map.put(id, circuitBreaker);
        }

        return circuitBreaker;
    }


    private CircuitBreaker(String id) {
        this.id = id;
    }

    public void setMaxFailCount(int maxFailCount) {
        this.maxFailCount = maxFailCount;
    }

    public void timeout(WorkEvent e) {
        failCount++;
        lastFailTime = System.currentTimeMillis();
    }

    public void error(WorkEvent e) {
        failCount++;
        lastFailTime = System.currentTimeMillis();
    }



    public boolean check(WorkEvent event) {

        long gap = System.currentTimeMillis() - lastFailTime;

        if (gap > retryTimeGap) {
            return  true;
        }

        if (maxFailCount <= failCount) {
            System.err.println("===2");
            return true;
        } else {
            System.err.println("===3");
            return false;
        }

    }

}
