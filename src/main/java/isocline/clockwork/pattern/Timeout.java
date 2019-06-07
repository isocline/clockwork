package isocline.clockwork.pattern;

import isocline.clockwork.*;

import java.util.HashMap;
import java.util.Map;

public class Timeout implements WorkFlowPattern {


    private long timeout;

    private String timeoutEventName;

    private static Map<String, Timeout> map = new HashMap<>();

    public static Timeout setup(long timeout) {

        return new Timeout(timeout);
    }


    private Timeout(long timeout) {
        this.timeout = timeout;
        this.timeoutEventName = "timeout-" + this.hashCode();
    }


    public void timeout(WorkEvent e) {
        e.getPlan().setError(new FlowProcessException("timeout"));
    }


    @Override
    public void beforeFlow(WorkFlow flow) {

        flow.fireEvent("error::" + timeoutEventName, this.timeout);
    }

    @Override
    public void afterFlow(WorkFlow flow) {

        flow.onError(this.timeoutEventName).next(this::timeout).finish();
    }


}
