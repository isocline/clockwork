package isocline.clockwork.flow;

import isocline.clockwork.WorkFlow;

public class WorkFlowFactory {


    public static WorkFlow createWorkFlow() {
        return new WorkFlowImpl();
    }
}
