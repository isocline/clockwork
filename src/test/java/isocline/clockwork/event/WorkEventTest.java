package isocline.clockwork.event;

import isocline.clockwork.WorkEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorkEventTest {


    @Test
    public void testBasic() {

        WorkEvent event = WorkEventFactory.create("evt");

        assertEquals("evt", event.getEventName());

        assertEquals(null, event.getAttribute("key1"));

        event.removeAttribute("key1");


        event.setAttribute("key1", "val1");
        assertEquals("val1", event.getAttribute("key1"));

        event.setAttribute("key1", "val2");
        assertEquals("val2", event.getAttribute("key1"));

        event.removeAttribute("key1");
        assertEquals(null, event.getAttribute("key1"));

    }

    @Test
    public void testChildEvent() {

        WorkEvent event = WorkEventFactory.create("evt");

        assertEquals("evt", event.getEventName());

        event.removeAttribute("key1");
        event.setAttribute("key1", "val1");
        assertEquals("val1", event.getAttribute("key1"));


        WorkEvent event2 =event.createChild("evt2");
        assertEquals("evt2", event2.getEventName());

        assertEquals(null, event2.getAttribute("key1"));


        //event2.setAttribute("key1", "val2");
        assertEquals("val1", event2.root().getAttribute("key1"));

    }
}
