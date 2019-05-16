package isocline.clockwork.object;

import isocline.clockwork.*;

public interface WorkObject extends Work {


    void processFlow(ProcessFlow flow);




    default WorkSchedule regist(WorkProcessor worker, ProcessFlow flow) {

        processFlow(flow);

        WorkSchedule schedule = worker.createSchedule(this);

        return schedule;
    }


    /**
     *
     * @param worker
     */
    default void execute(WorkProcessor worker) {

        ProcessFlow flow = new ProcessFlow();

        WorkSchedule schedule = regist(worker, flow);

        schedule.setProcessFlow(flow).activate();
    }


    @Override
    default long execute(EventInfo event) throws InterruptedException {

        //logger.debug("---- exec ---");

        ProcessFlow flow = event.getWorkSchedule().getProcessFlow();

        String eventName = event.getEventName();
        FunctionExecutor exec = null;

        if(eventName!=null) {
            exec = flow.getExecutor(eventName);



            if (exec != null) {

                try {
                    exec.execute();
                }catch (Exception e) {
                    e.printStackTrace();
                    event.getWorkSchedule().raiseLocalEvent(event.setEventName("error"));

                }

                if (exec.isLastExecutor()) {
                    return TERMINATE;
                } else {
                    return WAIT;
                }
            }
        }


        exec = (FunctionExecutor) event.getAttribute("exec.func");
        if (exec != null) {
            event.removeAttribute("exec.func");
            try {
                exec.execute();
            }catch (Exception e) {
                e.printStackTrace();
                event.getWorkSchedule().raiseLocalEvent(event.setEventName("error"));
            }

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {


                //event.getWorkSchedule().getWorkProcessor().raiseEvent(eventNm, event);
                event.getWorkSchedule().raiseLocalEvent(event.setEventName(eventNm));
            }
            if (exec.isLastExecutor()) {
                return TERMINATE;
            } else {
                return WAIT;
            }
        }


        exec = flow.getNextExecutor();
        if (exec != null) {

            if (exec.isAsync()) {

                EventInfo newEvent = new EventInfo();
                newEvent.setAttribute("exec.func", exec);
                event.getWorkSchedule().raiseLocalEvent(newEvent);

                return 1;
            }


            try {
                exec.execute();
            }catch (Exception e) {
                e.printStackTrace();
                event.getWorkSchedule().raiseLocalEvent(event.setEventName("error"));
                return WAIT;
            }

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {

                //event.getWorkSchedule().getWorkProcessor().raiseEvent(eventNm, event);
                event.getWorkSchedule().raiseLocalEvent(event.setEventName(eventNm));
            }

            if (exec.isLastExecutor()) {
                return TERMINATE;
            }

        } else {
            return WAIT;
        }
        return 1;
    }


}
