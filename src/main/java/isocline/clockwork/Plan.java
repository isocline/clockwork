/*
 * Copyright 2018 The Isocline Project
 *
 * The Isocline Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package isocline.clockwork;

import isocline.clockwork.event.EventRepository;
import isocline.clockwork.event.EventSet;
import isocline.clockwork.event.WorkEventFactory;
import isocline.clockwork.flow.WorkFlowFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * Process various settings related to the execution of the job.
 * You can set scheduling related to tasks such as start time, end time, and repeat time.
 *
 * @see isocline.clockwork.Work
 */
public class Plan {

    private static final long UNDEFINED_INTERVAL = -1;

    private static final long PREEMPTIVE_CHECK_MILLITIME = 2;


    private String workUUID;

    private long waitingTime = 0;

    private long nextExecuteTime = 0;


    private boolean isDefinedStartTime = false;

    private long workEndTime = 0;

    private boolean isLock = false;

    private boolean isActivated = false;

    private long intervalTime = UNDEFINED_INTERVAL;

    private long jitter = 0;


    private boolean isStrictMode = false;

    private boolean isBetweenStartTimeMode = true;

    private boolean needWaiting = false;

    private boolean isEventBindding = false;


    private Work work;

    private WorkSession workSession = null;

    private Object lockOwner = null;

    private WorkProcessor workProcessor = null;

    private LinkedList<WorkEvent> eventList = new LinkedList<WorkEvent>();


    private WorkFlow workFlow = null;

    private ExecuteEventChecker executeEventChecker = null;

    private Throwable error = null;

    private Consumer consumer = null;

    Plan(WorkProcessor workProcessor, Work work) {
        this.workProcessor = workProcessor;
        this.work = work;

        this.workUUID = UUID.randomUUID().toString();
    }


    /**
     * Returns a ID of Plan
     *
     * @return a ID of Plan
     */
    public String getId() {
        return this.workUUID;
    }

    //
    private void checkLocking() throws RuntimeException {
        if (isLock) {
            throw new RuntimeException("Changing settings is prohibited.");
        }

    }


    void adjustWaiting() throws InterruptedException {
        if (this.isStrictMode && needWaiting) {

            synchronized (workUUID) {
                for (int i = 0; i < 10000000; i++) {

                    if (nextExecuteTime <= System.currentTimeMillis()) {
                        return;
                    }
                }
            }
        }
    }


    long getPreemptiveMilliTime() {
        if (this.isStrictMode) {
            return PREEMPTIVE_CHECK_MILLITIME;
        } else {
            return 0;
        }

    }

    long checkRemainMilliTime() {
        needWaiting = false;

        if (!isActivated) {
            return 0;
            //throw new RuntimeException("service end");
        }
        if (isUntilEndTime()) {
            return Long.MAX_VALUE;
        }


        if (this.waitingTime == 0) {
            if (this.isStrictMode) {
                needWaiting = true;
            }

            return 0;
        } else if (this.eventList.size() > 0) {
            return 0;
        } else if (this.nextExecuteTime > 0) {

            long t1 = this.nextExecuteTime - System.currentTimeMillis();


            if (t1 <= 0) {
                return t1;
            } else if (this.isStrictMode && t1 < this.getPreemptiveMilliTime()) {
                needWaiting = true;
                return t1;
            }

            return t1;

        }


        return Clock.HOUR;
    }


    long getNextExecuteTime() {
        return this.nextExecuteTime;
    }


    boolean isUntilEndTime() {
        if (this.workEndTime > 0) {
            if (System.currentTimeMillis() >= this.workEndTime) {
                return true;
            }
        }

        return false;
    }


    // public method


    public void lock(Object lockOwner) throws IllegalAccessException {
        if (this.lockOwner != null) {
            throw new IllegalAccessException("Already locking");
        }
        this.lockOwner = lockOwner;
        this.isLock = true;
    }


    /**
     * Unlock
     *
     * @param lockOwner The object that performed the lock
     * @throws IllegalAccessException If the Lock object and the input object are not the same
     */
    public void unlock(Object lockOwner) throws IllegalAccessException {
        if (lockOwner == this.lockOwner) {
            this.isLock = false;
            this.lockOwner = null;
        } else {
            throw new IllegalAccessException("not lock owner");
        }
    }


    /**
     * Sets a {@link Work} implement object.
     *
     * @param workObject an instance of WorkObject
     */
    public void setWorkObject(Work workObject) {
        this.work = workObject;
    }

    /**
     *
     * @return a instance of Work
     */
    public Work getWorkObject() {
        return this.work;
    }


    public Plan startTime(long nextExecuteTime) {

        this.isDefinedStartTime = true;
        return setStartTime(nextExecuteTime);
    }

    /**
     * Sets a start time for this schedule
     *
     * @param nextExecuteTime the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     * @return an instance of Plan
     */
    private Plan setStartTime(long nextExecuteTime) {

        if (waitingTime == 0) {
            waitingTime = 1;
        }

        this.nextExecuteTime = nextExecuteTime;

        return this;
    }

    public Plan startDelayTime(long waitTime) {
        checkLocking();
        this.waitingTime = waitTime;

        return this;
    }

    /**
     * Adjust a delay time
     *
     * @param waitTime a milliseconds for timeout
     * @return an instance of Plan
     */
    Plan adjustDelayTime(long waitTime) {


        this.waitingTime = waitTime;

        if (waitTime < 0) {
            this.nextExecuteTime = UNDEFINED_INTERVAL;


        } else {

            if (this.isBetweenStartTimeMode && this.nextExecuteTime > 0) {

                long tmp = System.currentTimeMillis() - nextExecuteTime;


                if (tmp > 0) {
                    long x = (long) Math.ceil((double) tmp / (double) waitTime);

                    setStartTime(this.nextExecuteTime + waitTime * (x));

                    return this;
                } else if (tmp == 0) {
                    setStartTime(this.nextExecuteTime + waitTime);
                    return this;
                }


            }

            long crntTime = System.currentTimeMillis();
            long adjCrntTime = crntTime;
            if (this.isStrictMode) {
                for (int i = 0; i < 5; i++) {
                    long adjTime = (crntTime - (crntTime % 1000)) + this.jitter + i * 1000;
                    long nextTime = adjTime + waitTime;

                    if ((crntTime + this.jitter) < nextTime) {
                        adjCrntTime = adjTime;
                        break;
                    }
                }
            }


            long chkTime = adjCrntTime + waitTime;
            if (this.nextExecuteTime < chkTime) {
                setStartTime(chkTime);

            } else {
                //System.err.println("overtime : "+this.nextExecuteTime);
            }


        }


        return this;
    }


    /**
     * Returns an interval time
     *
     * @return a milliseconds time for interval
     */
    public long getIntervalTime() {
        return this.intervalTime;
    }


    /**
     * Sets an interval time
     *
     * @param intervalTime a milliseconds time for interval
     * @return an instance of Plan
     */
    public Plan interval(long intervalTime) {

        checkLocking();

        this.intervalTime = intervalTime;

        return this;
    }


    /**
     *
     * @param intervalTime
     * @return
     */
    Plan adjustRepeatInterval(long intervalTime) {


        this.intervalTime = intervalTime;

        return startDelayTime(intervalTime);

    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime this date-time as a String, such as 2019-06-16T10:15:30Z or 2019-06-16T10:15:30+01:00[Europe/Paris].
     * @return an instance of Plan
     * @throws java.text.ParseException if date time format is not valid.
     */
    public Plan startTime(String isoDateTime) throws java.text.ParseException {

        this.isDefinedStartTime = true;

        return startTime(Clock.toDate(isoDateTime));
    }


    /**
     * Sets a start date time
     *
     * @param startDateTime Date of start
     * @return an instance of Plan
     */
    public Plan startTime(Date startDateTime) {

        this.isDefinedStartTime = true;

        this.setStartTime(startDateTime.getTime());
        return this;
    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime this date-time as a String, such as 2019-06-16T10:15:30Z or 2019-06-16T10:15:30+01:00[Europe/Paris].
     * @return an instance of Plan
     * @throws java.text.ParseException if date time format is not valid.
     */
    public Plan finishTime(String isoDateTime) throws java.text.ParseException {

        return finishTime(Clock.toDate(isoDateTime));
    }


    /**
     * Sets finish date time
     *
     * @param endDateTime Date of end
     * @return an instance of Plan
     */
    public Plan finishTime(Date endDateTime) {

        this.workEndTime = endDateTime.getTime();
        return this;
    }

    /**
     * Sets a finish time from now
     *
     * @param milliSeconds milliseconds
     * @return an instance of Plan
     */
    public Plan finishTimeFromNow(long milliSeconds) {
        this.workEndTime = System.currentTimeMillis() + milliSeconds;
        return this;
    }


    /**
     * @param className name of class
     * @return an instance of Plan
     * @throws ClassNotFoundException if the class cannot be located
     * @throws InstantiationException if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     */
    public Plan workSession(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        checkLocking();
        this.workSession = (WorkSession) Class.forName(className).newInstance();
        return this;
    }

    /**
     * Sets a {@link WorkSession}
     *
     * @param workSession an instance of WorkSession
     * @return an instance of Plan
     */
    public Plan workSession(WorkSession workSession) {
        checkLocking();

        this.workSession = workSession;
        return this;
    }


    /**
     * Returns a {@link WorkSession}
     *
     * @return an instance of WorkSession
     */
    public WorkSession getWorkSession() {

        if (this.workSession == null) {
            this.workSession = new BasicWorkSession();

        }

        return this.workSession;
    }


    WorkFlow getWorkFlow() {
        return this.workFlow;
    }


    ////////////////


    public void raiseLocalEvent(WorkEvent event) {

        this.workProcessor.addWorkSchedule(this, event);

    }


    public void raiseLocalEvent(WorkEvent event, long delayTime) {

        if(delayTime>0) {
            //this.workProcessor.workChecker

            this.getWorkProcessor().addWorkSchedule(this,event, delayTime);

        }else {
            raiseLocalEvent(event);
        }



    }


    public Plan bindEvent(String... eventNames) {
        checkLocking();

        for (String eventName : eventNames) {
            String[] subEventNames = eventRepository.setBindEventNames(eventName);
            for (String subEventName : subEventNames) {
                this.workProcessor.bindEvent(this, subEventName);
            }
        }
        this.isEventBindding = true;

        return this;
    }


    public Plan setStrictMode() {
        checkLocking();
        this.isStrictMode = true;
        return this;
    }

    public Plan setBetweenStartTimeMode(boolean isBetweenStartTimeMode) {
        checkLocking();
        this.isBetweenStartTimeMode = isBetweenStartTimeMode;
        return this;
    }

    public Plan jitter(long jitter) {
        checkLocking();
        this.jitter = jitter;
        return this;
    }

    public Plan setSleepMode() {
        checkLocking();
        this.startDelayTime(Work.WAIT);
        return this;
    }


    public Plan run() {
        Plan schedule = activate();

        try {
            schedule.block();
        }catch (InterruptedException ie) {

        }

        Throwable error = schedule.getError();
        if(error!=null) {
            RuntimeException runtimeException = new RuntimeException(error);
            throw runtimeException;
        }

        return schedule;
    }



    public Plan activate() {
        return activate(null);
    }


    /**
     * Actives a {@link Plan}
     * @param consumer  Consumer
     * @return an instance of Plan
     */
    public Plan activate(Consumer consumer) {
        if (isActivated) {
                throw new RuntimeException("Already activate!");
        }
        this.consumer = consumer;
        this.isActivated = true;


        try {
            if(this.work instanceof FlowableWork) {
                this.workFlow = WorkFlowFactory.createWorkFlow();

                FlowableWork fw = (FlowableWork) this.work;

                WorkFlow wf = this.workFlow
                        .next(fw::initialize);



                fw.defineWorkFlow(wf);

                if (!wf.isSetFinish()) {
                    wf.fireEvent(WorkFlow.FINISH,0);
                    wf.wait(WorkFlow.FINISH).finish();
                }
            }

        } catch (UnsupportedOperationException npe) {

        }

        if (this.isStrictMode && !this.isDefinedStartTime) {

            long startTime = Clock.nextSecond(900);
            this.setStartTime(startTime + this.waitingTime);
        } else if (this.waitingTime > 0) {
            this.adjustDelayTime(this.waitingTime);
        }


        if (this.waitingTime != Work.WAIT) {
            if (this.isStrictMode || this.isDefinedStartTime || this.waitingTime > 1 || isEventBindding) {
                this.workProcessor.addWorkSchedule(this, false);
            } else {
                this.workProcessor.addWorkSchedule(this, true);
            }

        }

        this.workProcessor.managedWorkCount.incrementAndGet();

        return this;
    }

    public Plan scheduleDescriptor(ScheduleDescriptor descriptor) {
        descriptor.build(this);
        return this;
    }

    /**
     * check activated
     *
     * @return True if Plan is activated
     */
    public boolean isSubscribed() {
        return isActivated;
    }


    /**
     * finish job
     *
     */
    synchronized public void finish() {
        if (this.isActivated) {
            this.isActivated = false;
            this.workProcessor.managedWorkCount.decrementAndGet();
        }
        if(this.consumer!=null) {
            WorkEvent originEvent = this.getOriginWorkEvent();
            Object result = WorkHelper.Get(originEvent);
            consumer.accept(result);
        }
        notifyAll();
    }


    synchronized public void block(long timeout) throws InterruptedException {
        wait(timeout);
    }



    synchronized public void block() throws InterruptedException{
        wait();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plan schedule = (Plan) o;

        return workUUID.equals(schedule.workUUID);
    }

    @Override
    public int hashCode() {
        return workUUID.hashCode();
    }


    public WorkProcessor getWorkProcessor() {
        return workProcessor;
    }


    public Plan executeEventChecker(ExecuteEventChecker checker) {
        this.executeEventChecker = checker;
        return this;
    }

    boolean isExecuteEnable(long time) {
        if (this.executeEventChecker != null) {
            return this.executeEventChecker.check(time);
        }

        return true;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }


///////////////////////////////////////////////////////////////////


    private WorkEvent originEvent = null;

    WorkEvent getOriginWorkEvent() {
        return this.originEvent;
    }

    WorkEvent getOriginWorkEvent(WorkEvent inputWorkEvent) {

        WorkEvent event = null;

        if(inputWorkEvent==null) {
            if(originEvent==null) {
                originEvent = WorkEventFactory.create();
                originEvent.setPlan(this);
            }
            event = originEvent;
        }else {
            if(originEvent==null) {
                originEvent = inputWorkEvent;
            }
            event = inputWorkEvent;
            event.setPlan(this);
        }

        return event;


    }


    private EventRepository eventRepository = new EventRepository();


    /**
     *
     *
     * @param eventName name of event
     * @return name of event
     */
    String getDeliverableEventName(String eventName) {


        EventSet eventSet = eventRepository.getEventSet(eventName);


        if (eventSet == null) {
            //System.err.println("getDeliverableEventName normal = "+eventName );
            return eventName;
        }


        if (eventSet.isRaiseEventReady(eventName)) {

            //System.err.println("getDeliverableEventName event="+eventName + " set="+eventSet + " fire="+eventSet.getEventSetName());
            return eventSet.getEventSetName();
        }


        return null;

    }


    ////////////////////////////


    ExecuteContext enterQueue(boolean isUserEvent, WorkEvent workEvent) {


        ExecuteContext ctx = new ExecuteContext(this, isUserEvent, workEvent);

        return ctx;
    }


    ExecuteContext enterQueue(boolean isUserEvent) {

        ExecuteContext ctx = new ExecuteContext(this, isUserEvent, null);


        return ctx;
    }


    /**
     * ExecuteContext for Queue
     */
    static class ExecuteContext {


        private Plan plan;

        private boolean isUserEvent = false;

        private WorkEvent workEvent;


        ExecuteContext(Plan plan, boolean isUserEvent, WorkEvent event) {

            this.plan = plan;
            this.isUserEvent = isUserEvent;

            this.workEvent = event;

            /*
            if(event!=null) {
                event.setPlan(plan);

            }
            */
        }


        boolean isExecuteImmediately() {
            if (isUserEvent) {
                this.isUserEvent = false;

                return true;
            }

            return false;
        }

        Plan getPlan() {

            //if (this.contextId == this.plan.contextCheckId)
            {
                return plan;
            }

            //return null;

        }

        WorkEvent getWorkEvent() {
            return this.workEvent;
        }


    }

}