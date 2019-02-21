package isocline.clockwork.object;

import isocline.clockwork.*;

public interface WorkObject extends Work {


    void processFlow(ProcessFlow flow);




    default WorkSchedule regist(ClockWorker worker, ProcessFlow flow) {

        processFlow(flow);

        WorkSchedule schedule = worker.createSchedule(this);

        return schedule;
    }


    /**
     *
     * @param worker
     */
    default void execute(ClockWorker worker) {

        ProcessFlow flow = new ProcessFlow();

        WorkSchedule schedule = regist(worker, flow);

        schedule.setProcessFlow(flow).setStartTime(1).activate();
    }


    @Override
    default long execute(EventInfo event) throws InterruptedException {

        //logger.debug("---- exec ---");

        ProcessFlow flow = event.getWorkSchedule().getProcessFlow();

        String eventName = event.getEventName();
        Executor exec = null;

        if(eventName!=null) {
            exec = flow.getExecutor(eventName);



            if (exec != null) {

                exec.execute();

                if (exec.isEndExecutor()) {
                    return FINISH;
                } else {
                    return SLEEP;
                }
            }
        }


        exec = (Executor) event.getAttribute("exec.func");
        if (exec != null) {
            event.removeAttribute("exec.func");
            exec.execute();

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {


                //event.getWorkSchedule().getClockWorker().raiseEvent(eventNm, event);
                event.getWorkSchedule().raiseLocalEvent(event.setEventName(eventNm));
            }
            if (exec.isEndExecutor()) {
                return FINISH;
            } else {
                return SLEEP;
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


            exec.execute();

            String eventNm = exec.getFireEventName();
            if (eventNm != null) {

                //event.getWorkSchedule().getClockWorker().raiseEvent(eventNm, event);
                event.getWorkSchedule().raiseLocalEvent(event.setEventName(eventNm));
            }

            if (exec.isEndExecutor()) {
                return FINISH;
            }

        } else {
            return SLEEP;
        }
        return 1;
    }


}
