package isocline.clockwork;

import isocline.clockwork.event.EventRepository;
import isocline.clockwork.event.EventSet;
import isocline.clockwork.object.Executor;

import java.util.ArrayList;
import java.util.List;

public class ProcessFlow extends ArrayList<Executor> {


    private Executor e;
    private int sequence = 0;

    private List<Executor> waiters = new ArrayList<Executor>();



    private EventRepository<String,Executor> eventRepository = new EventRepository();


    public void reset() {
        sequence = 0;
    }

    public ProcessFlow end() {
        if(e!=null) {
            e.setEnd(true);
        }

        return this;
    }


    public ProcessFlow run(Runnable runnable) {

        add(new Executor(runnable, false));

        return this;
    }

    public ProcessFlow runAsync(Runnable runnable) {

        add(new Executor(runnable, true));
        return this;
    }




    public ProcessFlow runAsync(Runnable runnable, String eventName) {
        e = new Executor(runnable, true);
        e.setFireEventName(eventName);

        add(e);

        return this;
    }





    public ProcessFlow runWait(Runnable runnable, String eventName) {

        String[] subEventNames = eventRepository.setBindEventNames(eventName);



        e = new Executor(runnable, false);
        e.setRecvEventName(eventName);

        waiters.add(e);

        eventRepository.put(eventName, e);
        for(String subEventName:subEventNames) {
            eventRepository.put(subEventName, e);
        }

        return this;
    }



    public Executor getExecutor(String eventName) {
        if(eventName==null) return null;


        /*
        Executor x =  this.eventRepository.get(eventName);
        System.err.println(eventName + " --> "+x);

        return x;
        */


        EventSet eventSet = eventRepository.getEventSet(eventName);



        if(eventSet==null || eventSet.isRaiseEventReady(eventName)) {
            return this.eventRepository.get(eventName);
        }

        return  null;


    }

    public Executor getNextExecutor() {

        //IndexOutOfBoundsException

        Executor exec = null;
        try {
            exec = this.get(this.sequence);
            //System.err.println("getNextExecutor:"+sequence );
        } catch (IndexOutOfBoundsException ie) {
            return null;
        }
        if (exec != null) {
            sequence++;
        }

        return exec;
    }

    public List<Executor> getWaiters() {
        return this.waiters;
    }


}
