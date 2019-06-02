package isocline.clockwork.flow.func;

import isocline.clockwork.WorkEvent;

@FunctionalInterface
public interface ReturnEventFunction {


    String checkFlow(WorkEvent event);
}
