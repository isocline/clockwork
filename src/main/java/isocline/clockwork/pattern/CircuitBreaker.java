package isocline.clockwork.pattern;

import isocline.clockwork.Clock;
import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkFlow;
import isocline.clockwork.WorkFlowPattern;

import java.util.HashMap;
import java.util.Map;

public class CircuitBreaker implements WorkFlowPattern {

    @Override
    public void beforeFlow(WorkFlow flow) {
        System.err.println("1 ---"+this.timeoutEventName);
        flow.fireEvent("error::"+timeoutEventName, this.timeout)
        .check(this::check);
    }

    @Override
    public void afterFlow(WorkFlow flow) {
        System.err.println("2 ---"+this.timeoutEventName);
        String cursor = flow.cursor();

        flow.onError(cursor, this.timeoutEventName).next(this::error).finish();
    }


    private String id;
    private int failCount;

    private long lastFailTime = 0;

    private int maxFailCount = 3;

    private long retryTimeGap = Clock.SECOND * 10;

    private long timeout = 3000;

    private String timeoutEventName;


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
        this.timeoutEventName = "timeout-"+this.hashCode();
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

        System.err.println("!!!!! RAISE ERROR count== "+failCount);
    }



    public boolean check(WorkEvent event) {

        System.err.println( "FAIL COUNT: "+failCount + "  max :"+maxFailCount);

        long gap = System.currentTimeMillis() - lastFailTime;

        if (gap > retryTimeGap) {
            System.err.println("? === FAIL T");
            return  true;
        }

        if (maxFailCount > failCount) {
            System.err.println("___ OK");
            return true;
        } else {
            event.getWorkSchedule().setError(new RuntimeException("circuit open"));
            System.err.println("? === FAIL 1");
            return false;
        }

    }



}
