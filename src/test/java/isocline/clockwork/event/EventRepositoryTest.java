package isocline.clockwork.event;

import isocline.clockwork.flow.FunctionExecutor;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EventRepositoryTest {

    EventRepository<String, Queue<FunctionExecutor>> eventRepository = new EventRepository();

    @Test
    public void testBasic() {

        String eventName = "event1";

        String[] subEventNames = eventRepository.setBindEventNames(eventName);

        assertEquals(1, subEventNames.length);
        assertEquals(eventName, subEventNames[0]);

    }

    @Test
    public void testMultiEvents() {

        String eventName = "event1&event2";

        String[] subEventNames = eventRepository.setBindEventNames(eventName);

        assertEquals(2, subEventNames.length);
        assertEquals("event1", subEventNames[0]);
        assertEquals("event2", subEventNames[1]);



        EventSet eventSet = eventRepository.getEventSet("event1");

        if (eventSet == null || eventSet.isRaiseEventReady("event1")) {
            fail();
        }

        eventSet = eventRepository.getEventSet("event3");

        if (eventSet == null || eventSet.isRaiseEventReady("event3")) {

        }else {
            fail();
        }

        eventSet = eventRepository.getEventSet("event2");

        if (eventSet == null || eventSet.isRaiseEventReady("event2")) {

        }else{
            fail();
        }
    }


    private void bindEventRepository(String eventName, FunctionExecutor functionExecutor) {


        Queue<FunctionExecutor> queue = this.eventRepository.get(eventName);
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<FunctionExecutor>();
            this.eventRepository.put(eventName, queue);
        }

        queue.add(functionExecutor);

    }
}
