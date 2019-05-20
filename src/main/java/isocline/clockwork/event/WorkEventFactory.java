package isocline.clockwork.event;

import isocline.clockwork.WorkEvent;


/**
 *
 * Factory class for WorkEvent object creation.
 *
 * @author Richard D. Kim
 */
public class WorkEventFactory {


    /**
     *
     * Create a implement object of WorkEvent
     *
     * @param eventName
     * @return
     */
    public static WorkEvent create(String eventName) {
        WorkEventImpl event = new WorkEventImpl(eventName);

        return event;
    }


    /**
     * Create a implement object of WorkEvent
     *
     * @return
     */
    public static WorkEvent create() {
        WorkEventImpl event = new WorkEventImpl();


        return event;
    }
}
