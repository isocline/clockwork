package isocline.clockwork.flow.func;

import isocline.clockwork.WorkEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FunctionalInterface
public interface ExtendRunnableFunction {


    void run(WorkEvent event) throws Throwable;





}
