package isocline.clockwork.flow.func;

import isocline.clockwork.WorkEvent;

@FunctionalInterface
public interface CheckFunction {


    boolean check(WorkEvent event);
}
