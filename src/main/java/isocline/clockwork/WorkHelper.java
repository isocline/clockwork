package isocline.clockwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkHelper {


    public static Plan reflow(AbstractFlowableWork workFlow) {
        return WorkProcessor.main().reflow(workFlow);
    }

    public static List GetResultList(WorkEvent e) {

        WorkEvent event = e.origin();
        if(event==null) {
            event = e;
        }

        String resultKey = "result::" + event.hashCode();

        List list = null;
        synchronized (event) {
            list = (List) event.getAttribute(resultKey);
            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>());

                event.setAttribute(resultKey, list);
            }
        }

        return list;
    }


    public static Object Get(WorkEvent e) {
        WorkEvent event = e.origin();
        if(event==null) {
            event = e;
        }

        String resultKey = "result::" + event.hashCode()+"<Mono>";


        return event.getAttribute(resultKey);

    }


    public static void Return(WorkEvent e, Object result) {

        WorkEvent event = e.origin();
        if(event==null) {
            event = e;
        }

        String resultKey = "result::" + event.hashCode();

        event.setAttribute(resultKey+"<Mono>",result);

        List list = null;
        synchronized (event) {
            list = (List) event.getAttribute(resultKey);
            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>());

                event.setAttribute(resultKey, list);
            }
        }

        list.add(result);
    }
}
